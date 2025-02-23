package frc.robot.subsystems;
package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * This subsystem handles managing the Template.
 * It is responsible for doing some stuff.
 */
public class MotorSubsystem extends SubsystemBase {
	private Motor instance;
  //private and public variables defined here

  /**
	 * Returns the instance Motor subsystem.
	 * The purpose of this is to only create an instance if one does not already exist.
	 * @Motor instance
	 */
  public Motor getInstance() {
		if (instance == null)
			instanceMotor();
		return instance;
	}
  
  Motor() {
    //initialize values for private and public variables, etc.
  	
    init();
  }

  
  /**
   * The init method resets and operational state of the subsystem
   */
  public void init() {
    // set initial stuff, etc.
  }
  
  @Override
  public void periodic() {
  }

}
