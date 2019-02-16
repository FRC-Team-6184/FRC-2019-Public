/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6184.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PWMTalonSRX;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import org.usfirst.frc.team6184.robot.GripPipeline;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Solenoid;
/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends IterativeRobot {
	private DifferentialDrive m_myRobot;
	private Joystick m_rightStick;
	public static GripPipeline Pipe = new GripPipeline();
	DoubleSolenoid exampleDouble = new DoubleSolenoid(0, 7);  
	Solenoid Cam = new Solenoid(1);
	//AnalogGyro gyro= new AnalogGyro(0);
    Compressor c = new Compressor();
    private XboxController xbox;
   // BuiltInAccelerometer Built =new  BuiltInAccelerometer();
    Spark lift = new Spark(3);
    
    DigitalInput topLimitSwitch = new DigitalInput(8);
    PWMTalonSRX m_frontLeft = new PWMTalonSRX(1);
    Talon m_rearLeft = new Talon(2);
    DigitalInput botLimitSwitch = new DigitalInput(9);
    Thread m_visionThread;
    SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);
    
	@Override
	
	public void robotInit() {
		
		 
		m_myRobot = new DifferentialDrive(m_left, new PWMTalonSRX(0));
		m_rightStick = new Joystick(1);
		xbox = new XboxController(0);
		CameraServer.getInstance().startAutomaticCapture();
		 
		
	}

	@Override
	public void teleopPeriodic() {
			
		//double Accellx = Built.getX();
		//double AccellY = Built.getY();
		//double Gravity = Built.getZ();
		//double angle = gyro.getRate();
	//	System.out.println(angle);
		
		boolean clickedA = xbox.getAButton();
        boolean clickedB = xbox.getBButton();
        boolean clickedY = xbox.getYButton();
        boolean clickedX = xbox.getXButton();

            boolean clicked7 = xbox.getBButton();
            
            Double driveSpeed =  m_rightStick.getThrottle();
            System.out.println(driveSpeed);
    		//System.out.println(Gravity);
		
		//m_myRobot.arcadeDrive( m_rightStick.getY()* driveSpeed,m_rightStick.getTwist()*0.7);
		m_myRobot.curvatureDrive(m_rightStick.getY()*driveSpeed,m_rightStick.getZ()*0.7,true);

		
		if (clickedB) {
			c.setClosedLoopControl(true);
		}
		else { c.setClosedLoopControl(false);
		}
		
	if (clickedX) {
		Cam.set(true);
	}
		
	if (clickedY){
        exampleDouble.set(DoubleSolenoid.Value.kForward);
     }
     else if (!clickedY && clickedA){
        exampleDouble.set(DoubleSolenoid.Value.kReverse);
     }
	
     else           {    
        exampleDouble.set(DoubleSolenoid.Value.kOff);
     }
	if (clickedA) {
		xbox.setRumble(RumbleType.kLeftRumble, 1);}
		else { xbox.setRumble(RumbleType.kLeftRumble, 0);
		
		}
	
        
            lift.set(xbox.getY(Hand.kRight));
         
	}
	
	
         
	}










