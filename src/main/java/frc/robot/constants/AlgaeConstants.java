package frc.robot.constants;

public class AlgaeConstants {
  public static final int kIntakeMotorCanId = 5;
  public static final int kPivotMotorCanId = 4;

  public static final class ArmSetpoints {
    public static final double kStow = 0;
    public static final double kHold = 3;
    public static final double kDown = 17;
    public static final double kFeed = 10;
  } 

  public static final class IntakeSetpoints {
    public static final double kForward = 0.5;
    public static final double kReverse = -0.7;
    public static final double kHold = 0.2;
  }
}
