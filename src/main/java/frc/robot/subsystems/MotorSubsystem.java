// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorSubsystem extends SubsystemBase {

  private static MotorSubsystem instance;
  private SparkMax m_motor;

  public static MotorSubsystem getInstance() {
    if (instance == null)
      instance = new MotorSubsystem();
    return instance;
  }

  /** Creates a new MotorSubsystem. */
  public MotorSubsystem() {

    init();
  }

  public void init() {
    SparkMaxConfig defaultConfig = new SparkMaxConfig();
    defaultConfig
      .smartCurrentLimit(50)
      .idleMode(IdleMode.kBrake)
      .inverted(false);

      m_motor = new SparkMax(2, MotorType.kBrushless);
      m_motor.configure(defaultConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void runMotor() {
    m_motor.set(0.5);
  }
  public void stopMotor() {
    m_motor.set(0);
  }
}
