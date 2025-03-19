package frc.robot.constants;

import edu.wpi.first.math.util.Units;

public class SimRobotConstants {
  public static final double kPixelsPerMeter = 20;

  public static final double kElevatorGearing = 25; // 25:1
  public static final double kCarriageMass =
      4.3 + 3.15 + 0.151; // Kg, arm + elevator stage + chain
  public static final double kElevatorDrumRadius = 0.0328 / 2.0; // m
  public static final double kMinElevatorHeightMeters = 0.922; // m
  public static final double kMaxElevatorHeightMeters = 1.62; // m

  public static final double kArmReduction = 60; // 60:1
  public static final double kArmLength = 0.433; // m
  public static final double kArmMass = 4.3; // Kg
  public static final double kMinAngleRads =
      Units.degreesToRadians(-50.1); // -50.1 deg from horiz
  public static final double kMaxAngleRads =
      Units.degreesToRadians(40.9 + 180); // 40.9 deg from horiz

  public static final double kIntakeReduction = 135; // 135:1
  public static final double kIntakeLength = 0.4032262; // m
  public static final double kIntakeMass = 5.8738; // Kg
  public static final double kIntakeMinAngleRads = Units.degreesToRadians(80);
  public static final double kIntakeMaxAngleRads = Units.degreesToRadians(180);
  public static final double kIntakeShortBarLength = 0.1524;
  public static final double kIntakeLongBarLength = 0.3048;
  public static final double kIntakeBarAngleRads = Units.degreesToRadians(-60);
}
