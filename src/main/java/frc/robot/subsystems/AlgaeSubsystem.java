// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.sim.SparkFlexSim;
import com.revrobotics.sim.SparkMaxSim;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RevConfigs;
import frc.robot.constants.AlgaeConstants;
import frc.robot.constants.SimRobotConstants;

public class AlgaeSubsystem extends SubsystemBase {
  // Initialize arm SPARK. We will use MAXMotion position control for the arm, so we also need to
  // initialize the closed loop controller and encoder.

  //private SparkFlex armMotor =
  private SparkMax armMotor =
    //new SparkFlex(AlgaeConstants.kPivotMotorCanId, MotorType.kBrushless);
    new SparkMax(AlgaeConstants.kPivotMotorCanId, MotorType.kBrushless);
  private SparkClosedLoopController armController = armMotor.getClosedLoopController();
  private RelativeEncoder armEncoder = armMotor.getEncoder();

  // Initialize intake SPARK. We will use open loop control for this so we don't need a closed loop
  // controller like above.
  private SparkFlex intakeMotor =
      new SparkFlex(AlgaeConstants.kIntakeMotorCanId, MotorType.kBrushless);

  // Member variables for subsystem state management
  private boolean stowWhenIdle = true;
  private boolean wasReset = false;

  // Simulation setup and variables
  private DCMotor armMotorModel = DCMotor.getNeoVortex(1);
  private SparkMaxSim armMotorSim;
  private final SingleJointedArmSim m_intakeSim =
      new SingleJointedArmSim(
          armMotorModel,
          SimRobotConstants.kIntakeReduction,
          SingleJointedArmSim.estimateMOI(
              SimRobotConstants.kIntakeLength, SimRobotConstants.kIntakeMass),
          SimRobotConstants.kIntakeLength,
          SimRobotConstants.kIntakeMinAngleRads,
          SimRobotConstants.kIntakeMaxAngleRads,
          true,
          SimRobotConstants.kIntakeMinAngleRads,
          0.0,
          0.0);

  // Mechanism2d setup for subsytem
  private final Mechanism2d m_mech2d = new Mechanism2d(50, 50);
  private final MechanismRoot2d m_mech2dRoot = m_mech2d.getRoot("Ball Intake Root", 28, 3);
  private final MechanismLigament2d intakePivotMechanism =
      m_mech2dRoot.append(
          new MechanismLigament2d(
              "Intake Pivot",
              SimRobotConstants.kIntakeShortBarLength
                  * SimRobotConstants.kPixelsPerMeter,
              Units.radiansToDegrees(SimRobotConstants.kIntakeMinAngleRads)));

  @SuppressWarnings("unused")
  private final MechanismLigament2d intakePivotSecondMechanism =
      intakePivotMechanism.append(
          new MechanismLigament2d(
              "Intake Pivot Second Bar",
              SimRobotConstants.kIntakeLongBarLength
                  * SimRobotConstants.kPixelsPerMeter,
              Units.radiansToDegrees(SimRobotConstants.kIntakeBarAngleRads)));

  public AlgaeSubsystem() {
    /*
     * Apply the configuration to the SPARKs. 
     *
     * kResetSafeParameters is used to get the SPARK to a known state. This
     * is useful in case the SPARK is replaced.
     *
     * kPersistParameters is used to ensure the configuration is not lost when
     * the SPARK loses power. This is useful for power cycles that may occur
     * mid-operation.
     */
    intakeMotor.configure(
        RevConfigs.AlgaeSubsystem.intakeConfig,
        ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);
    armMotor.configure(
        RevConfigs.AlgaeSubsystem.armConfig,
        ResetMode.kResetSafeParameters,
        PersistMode.kPersistParameters);

    // Display mechanism2d
    SmartDashboard.putData("Algae Subsystem", m_mech2d);

    // Zero arm encoder on initialization
    armEncoder.setPosition(0);

    // Initialize Simulation values
     armMotorSim = new SparkMaxSim(armMotor, armMotorModel); 
    //armMotorSim = new SparkMaxSim(armMotor, armMotorModel);
  }
  

  /** Zero the arm encoder when the user button is pressed on the roboRIO */
  private void zeroOnUserButton() {
    if (!wasReset && RobotController.getUserButton()) {
      // Zero the encoder only when button switches from "unpressed" to "pressed" to prevent
      // constant zeroing while pressed
      wasReset = true;
      armEncoder.setPosition(0);
    } else if (!RobotController.getUserButton()) {
      wasReset = false;
    }
  }

  /**
   * Command to run the algae intake. This will extend the arm to its "down" position and run the
   * motor at its "forward" power to intake the ball.
   *
   * <p>This will also update the idle state to hold onto the ball when this command is not running.
   */
  public Command runIntakeCommand() {
    return this.run(
        () -> {
          stowWhenIdle = false;
          setIntakePower(AlgaeConstants.IntakeSetpoints.kForward);
          setIntakePosition(AlgaeConstants.ArmSetpoints.kDown);
        });
  }

  /**
   * Command to run the algae intake in reverse. This will extend the arm to its "hold" position and
   * run the motor at its "reverse" power to eject the ball.
   *
   * <p>This will also update the idle state to stow the arm when this command is not running.
   */
  public Command reverseIntakeCommand() {
    return this.run(
        () -> {
          stowWhenIdle = true;
          setIntakePower(AlgaeConstants.IntakeSetpoints.kReverse);
          setIntakePosition(AlgaeConstants.ArmSetpoints.kHold);
        });
  }

  /** Command to force the subsystem into its "stow" state. */
  public Command stowCommand() {
    return this.runOnce(
        () -> {
          stowWhenIdle = true;
        });
  }

  /**
   * Command to run when the intake is not actively running. When in the "hold" state, the intake
   * will stay in the "hold" position and run the motor at its "hold" power to hold onto the ball.
   * When in the "stow" state, the intake will stow the arm in the "stow" position and stop the
   * motor.
   */
  public Command idleCommand() {
    return this.run(
        () -> {
          if (stowWhenIdle) {
            setIntakePower(0.0);
            setIntakePosition(AlgaeConstants.ArmSetpoints.kStow);
          } else {
            setIntakePower(AlgaeConstants.IntakeSetpoints.kHold);
            setIntakePosition(AlgaeConstants.ArmSetpoints.kHold);
          }
        });
  }

  /** Set the intake motor power in the range of [-1, 1]. */
  private void setIntakePower(double power) {
    intakeMotor.set(power);
  }

  /** Set the arm motor position. This will use closed loop position control. */
  private void setIntakePosition(double position) {
    armController.setReference(position, ControlType.kPosition);
  }

  @Override
  public void periodic() {
    zeroOnUserButton();

    // Display subsystem values
    SmartDashboard.putNumber("Algae/Arm/Position", armEncoder.getPosition());
    SmartDashboard.putNumber("Algae/Intake/Applied Output", intakeMotor.getAppliedOutput());

    // Update mechanism2d
    intakePivotMechanism.setAngle(
        Units.radiansToDegrees(SimRobotConstants.kIntakeMinAngleRads)
            + Units.rotationsToDegrees(
                armEncoder.getPosition() / SimRobotConstants.kIntakeReduction));
  }

  /** Get the current drawn by each simulation physics model */
  public double getSimulationCurrentDraw() {
    return m_intakeSim.getCurrentDrawAmps();
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
    m_intakeSim.setInput(armMotorSim.getAppliedOutput() * RobotController.getBatteryVoltage());

    // Next, we update it. The standard loop time is 20ms.
    m_intakeSim.update(0.020);

    // Iterate the arm SPARK simulation
    armMotorSim.iterate(
        Units.radiansPerSecondToRotationsPerMinute(
            m_intakeSim.getVelocityRadPerSec() * SimRobotConstants.kArmReduction),
        RobotController.getBatteryVoltage(),
        0.02);

    // SimBattery is updated in Robot.java
  }
}
