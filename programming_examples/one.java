package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Basic FTC Robot TeleOp Program
 * This program controls a simple robot with:
 * - 4 drive motors (mecanum or tank drive)
 * - 1 arm motor
 * - 1 claw servo
 */
@TeleOp(name = "Basic Robot Control", group = "TeleOp")
public class BasicTeleOp extends OpMode {
    
    // Hardware components - these represent the physical parts of our robot
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor armMotor;
    private Servo clawServo;
    
    // Variables to control robot behavior
    private double drivePower = 0.8;    // How fast the robot moves (80% power)
    private double armPower = 0.5;      // How fast the arm moves (50% power)
    private double clawOpenPosition = 0.7;   // Servo position when claw is open
    private double clawClosedPosition = 0.2; // Servo position when claw is closed
    
    /**
     * This method runs ONCE when you press the INIT button on the phone
     * We use it to set up our robot's hardware
     */
    @Override
    public void init() {
        // Connect our code variables to the actual robot hardware
        // The names in quotes must match what you configured on the phone
        frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left");
        frontRightMotor = hardwareMap.get(DcMotor.class, "front_right");
        backLeftMotor = hardwareMap.get(DcMotor.class, "back_left");
        backRightMotor = hardwareMap.get(DcMotor.class, "back_right");
        armMotor = hardwareMap.get(DcMotor.class, "arm_motor");
        clawServo = hardwareMap.get(Servo.class, "claw_servo");
        
        // Set motor directions - some motors may be mounted backwards
        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setDirection(DcMotor.Direction.FORWARD);
        
        // Make sure all motors start stopped
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
        armMotor.setPower(0);
        
        // Set the claw to a middle position
        clawServo.setPosition(0.5);
        
        // Tell the drivers that initialization is complete
        telemetry.addData("Status", "Robot is ready to start!");
        telemetry.update();
    }
    
    /**
     * This method runs REPEATEDLY after you press PLAY
     * It reads controller input and moves the robot accordingly
     */
    @Override
    public void loop() {
        // Get input from the game controller
        double leftStickY = -gamepad1.left_stick_y;   // Forward/backward (negative because Y axis is flipped)
        double leftStickX = gamepad1.left_stick_x;    // Left/right strafe
        double rightStickX = gamepad1.right_stick_x;  // Rotation
        
        // Calculate power for each wheel using mecanum drive math
        double frontLeftPower = leftStickY + leftStickX + rightStickX;
        double frontRightPower = leftStickY - leftStickX - rightStickX;
        double backLeftPower = leftStickY - leftStickX + rightStickX;
        double backRightPower = leftStickY + leftStickX - rightStickX;
        
        // Make sure no wheel power exceeds 100% (normalize if necessary)
        double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                                  Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;
        }
        
        // Apply the calculated power to the motors, scaled by our drive power setting
        frontLeftMotor.setPower(frontLeftPower * drivePower);
        frontRightMotor.setPower(frontRightPower * drivePower);
        backLeftMotor.setPower(backLeftPower * drivePower);
        backRightMotor.setPower(backRightPower * drivePower);
        
        // Control the arm with the right bumper and trigger
        if (gamepad1.right_bumper) {
            // Right bumper raises the arm
            armMotor.setPower(armPower);
        } else if (gamepad1.right_trigger > 0.1) {
            // Right trigger lowers the arm
            armMotor.setPower(-armPower);
        } else {
            // No input - stop the arm
            armMotor.setPower(0);
        }
        
        // Control the claw with A and B buttons
        if (gamepad1.a) {
            // A button closes the claw
            clawServo.setPosition(clawClosedPosition);
        } else if (gamepad1.b) {
            // B button opens the claw
            clawServo.setPosition(clawOpenPosition);
        }
        
        // Display information on the phone screen for debugging
        telemetry.addData("Drive Power", "%.2f", drivePower);
        telemetry.addData("Left Stick", "X: %.2f, Y: %.2f", leftStickX, leftStickY);
        telemetry.addData("Right Stick X", "%.2f", rightStickX);
        telemetry.addData("Arm Power", "%.2f", armMotor.getPower());
        telemetry.addData("Claw Position", "%.2f", clawServo.getPosition());
        telemetry.addData("Controls", "Left stick: drive, Right stick: turn");
        telemetry.addData("Arm", "Right bumper: up, Right trigger: down");
        telemetry.addData("Claw", "A: close, B: open");
        telemetry.update();
    }
}