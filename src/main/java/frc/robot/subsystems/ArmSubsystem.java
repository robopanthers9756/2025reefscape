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

public class ArmSubsystem extends SubsystemBase {

  private static ArmSubsystem instance;
  private SparkMax a_motor;

  public static ArmSubsystem getInstance() {
    if (instance == null)
      instance = new ArmSubsystem();
    return instance;
  }

  /** Creates a new ArmSubsystem. */
  public ArmSubsystem() {

    init();
  }

  public void init() {
    SparkMaxConfig defaultConfig = new SparkMaxConfig();
    defaultConfig
      .smartCurrentLimit(50)
      .idleMode(IdleMode.kBrake)
      .inverted(false);

      a_motor = new SparkMax(2, MotorType.kBrushless);
      a_motor.configure(defaultConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void runArm() {
    a_motor.set(0.2);
  }
  public void reverseArm() {
    a_motor.set(-.2);
  }
  public void stopArm() {
    a_motor.set(0);
  }
}
