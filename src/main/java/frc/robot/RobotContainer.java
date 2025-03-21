// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;
import frc.robot.constants.OIConstants;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.CoralSubsystem;
import frc.robot.subsystems.AlgaeSubsystem;

//new motor subsystem
//import frc.robot.subsystems.ArmSubsystem;

import frc.robot.utils.InputAxis;

public class RobotContainer {
    public CoralSubsystem coral = CoralSubsystem.getInstance();
    public AlgaeSubsystem algae = new AlgaeSubsystem();
   // public ArmSubsystem arm = ArmSubsystem.getInstance();
    public double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    public double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
  
  //new motor subsystem
    //public ArmSubsystem motor = ArmSubsystem.getInstance();

    /* Setting up bindings for necessary control of the swerve drive platform */
    public final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * 0.1).withRotationalDeadband(MaxAngularRate * 0.1) // Add a 10% deadband
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive motors
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController driver = new CommandXboxController(OIConstants.JoyDriverID);

    //add operator controller
    private final CommandXboxController operator = new CommandXboxController(OIConstants.JoyOperatorID);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    final InputAxis m_fieldX = new InputAxis("Forward", driver::getLeftY)
        .withDeadband(OIConstants.kMinDeadband)
        .withInvert(true)
        // .withSlewRate(2,-2) //This allows robot to increase to max in 0.5s, and decrease from full to stop in 0.5s
        // written another way, this is number of units per second so 1 means it would increase to 100% over 1 second.
        .withSquaring(true);
    final InputAxis m_fieldY = new InputAxis("Strafe", driver::getLeftX)
        .withDeadband(OIConstants.kMinDeadband)
        .withInvert(true)
        .withSquaring(true);
    final InputAxis m_rotate = new InputAxis("Rotate", driver::getRightX)
        .withDeadband(OIConstants.kMinDeadband)
        .withInvert(true);


    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        // Note that X is defined as forward according to WPILib convention,
        // and Y is defined as to the left according to WPILib convention.
        drivetrain.setDefaultCommand(
            // Drivetrain will execute this command periodically
            drivetrain.applyRequest(() ->
                drive.withVelocityX(m_fieldX.getAsDouble() * MaxSpeed / 2) // Drive forward with negative Y (forward)
                    .withVelocityY(m_fieldY.getAsDouble() * MaxSpeed / 2) // Drive left with negative X (left)
                    .withRotationalRate(m_rotate.getAsDouble() * MaxAngularRate) // Drive counterclockwise with negative X (left)
            )
        );

        driver.a().whileTrue(drivetrain.applyRequest(() -> brake));
        driver.b().whileTrue(drivetrain.applyRequest(() ->
            point.withModuleDirection(new Rotation2d(-driver.getLeftY(), -driver.getLeftX()))
        ));

        operator.x()
        .onTrue(new InstantCommand(() -> coral.runMotor()))
        .onFalse(new InstantCommand(() -> coral.stopMotor()));
        operator.b()
        .onTrue(new InstantCommand(() -> coral.reverseMotor()))
        .onFalse(new InstantCommand(() -> coral.stopMotor()));


    // Right Trigger -> Run ball intake, set to leave out when idle
    operator
        .rightBumper()
        .whileTrue(algae.runIntakeCommand());

    // Left Trigger -> Run ball intake in reverse, set to stow when idle
    operator
        .leftBumper()
        .whileTrue(algae.reverseIntakeCommand());



        // Run SysId routines when holding back/start and X/Y.
        // Note that each routine should be run exactly once in a single log.
        driver.back().and(driver.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        driver.back().and(driver.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        driver.start().and(driver.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        driver.start().and(driver.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        // reset the field-centric heading on left bumper press
        driver.leftBumper().onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
