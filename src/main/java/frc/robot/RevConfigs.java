package frc.robot;

import com.revrobotics.spark.config.ClosedLoopConfig.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

public class RevConfigs {
  public static final class AlgaeSubsystem {
    public static final SparkFlexConfig intakeConfig = new SparkFlexConfig();
    //public static final SparkFlexConfig armConfig = new SparkFlexConfig();
    public static final SparkMaxConfig armConfig = new SparkMaxConfig();

    static {
      // Configure basic setting of the arm motor
      armConfig.smartCurrentLimit(40);

      /*
       * Configure the closed loop controller. We want to make sure we set the
       * feedback sensor as the primary encoder.
       */
      armConfig
          .closedLoop
          .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
          // Set PID values for position control. We don't need to pass a closed
          // loop slot, as it will default to slot 0.
          .p(0.1)
          .outputRange(-0.5, 0.5);

      // Configure basic settings of the intake motor
      intakeConfig.inverted(false).idleMode(IdleMode.kBrake).smartCurrentLimit(40);
    }
  }

}
