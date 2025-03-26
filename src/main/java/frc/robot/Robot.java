// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
//import edu.wpi.first.wpilibj2.command.WaitUntilCommand;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  public Robot() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run(); 
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    // m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // if (m_autonomousCommand != null) {
    //   m_autonomousCommand.schedule();
    // }
    
      // Drivetrain will execute this command periodically
      Command driveCommand = m_robotContainer.drivetrain.applyRequest(() ->
          m_robotContainer.drive.withVelocityX(0.3 * m_robotContainer.MaxSpeed / 2) // Drive forward with negative Y (forward)
              .withVelocityY(0.0 * m_robotContainer.MaxSpeed / 2) // Drive left with negative X (left)
              .withRotationalRate(0.0 * m_robotContainer.MaxAngularRate) // Drive counterclockwise with negative X (left)
      ).withTimeout(1);

      Command waitCommand = new WaitCommand(2.00);

      Command stopCommand = m_robotContainer.drivetrain.applyRequest(() ->
      m_robotContainer.drive.withVelocityX(0.0) // Drive forward with negative Y (forward)
          .withVelocityY(0.0) // Drive left with negative X (left)
          .withRotationalRate(0.0) // Drive counterclockwise with negative X (left)
  );

  Command autonomousCommand = new SequentialCommandGroup(
    driveCommand,
    waitCommand,
    stopCommand
  );



  

    autonomousCommand.schedule();

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}
}
