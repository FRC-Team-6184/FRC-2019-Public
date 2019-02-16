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
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.vision.VisionRunner;
import edu.wpi.first.wpilibj.vision.VisionThread;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Solenoid;
import java.util.ArrayList;


// import that doc
import org.usfirst.frc.team6184.robot.GripPipeline;
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
    private static final int IMG_WIDTH = 320;
    private static final int IMG_HEIGHT = 240;

	private VisionThread visionThread;
	 GripPipeline pipe = new GripPipeline();


	 private double centerX = 0.0;
	 
	 private final Object imgLock = new Object();





	@Override
	
	public void robotInit() {
		CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
		 
		m_myRobot = new DifferentialDrive(m_left, new PWMTalonSRX(0));
		m_rightStick = new Joystick(1);
		xbox = new XboxController(0);
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
		
		visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
			if (pipeline.filterContoursOutput().size()>=2) {
				Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
				Rect r2 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(1));
				Mat output = pipeline.resizeImageOutput();
				Imgproc.rectangle(output, new Point(r.x, r.y), new Point(r.x+r.width, r.y+r.height), new Scalar(255, 0, 0), 5);
				Imgproc.rectangle(output, new Point(r2.x, r2.y), new Point(r2.x+r2.width, r2.y+r2.height), new Scalar(0, 255, 0), 5);
       
				synchronized (imgLock) {
					centerX = (r.x + r2.x +(r2.width / 2)+(r.width/2))/2;
					
				}
			Imgproc.circle (
					output,                 //Matrix obj of the image
					new Point(centerX, 50),    //Center of the circle
					5,                    //Radius
					new Scalar(0, 0, 255),  //Scalar object for color
					10
				 );
				 outputStream.putFrame(output);
			}
		});
		visionThread.start();
	}

	@Override
	public void teleopPeriodic() {
		Cam.set(false);
		double centerX;
    synchronized (imgLock) {
        centerX = this.centerX;
    }
	double turn = centerX - (IMG_WIDTH / 2);
	
	System.out.println(turn);
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
		m_myRobot.curvatureDrive(0.3, turn * 0.005,true);
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
	
	
        
            lift.set(xbox.getY(Hand.kRight));
         System.out.println("TESTREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
	}
	
	public void autonomousInit() {
		teleopInit();
		
	}
   
	   public void autonomousPeriodic(){
		System.out.println("I'm gonna run in autonomous");
		teleopPeriodic();
	   }
	   
	   @Override
	   public void disabledInit(){
	   }
         
	}










