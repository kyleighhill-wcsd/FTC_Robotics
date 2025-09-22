# FTC Programming Quick Reference Guide

## Program Structure Template
```java
@TeleOp(name = "Your Program Name", group = "TeleOp")
public class YourProgram extends OpMode {
    // 1. Declare hardware
    private DcMotor motor;
    private Servo servo;
    
    @Override
    public void init() {
        // 2. Initialize hardware
        motor = hardwareMap.get(DcMotor.class, "motor_name");
        motor.setDirection(DcMotor.Direction.FORWARD);
    }
    
    @Override
    public void loop() {
        // 3. Read input, calculate, control robot
        double power = gamepad1.left_stick_y;
        motor.setPower(power);
    }
}
```

---

## Essential Hardware Types

| Hardware | Purpose | Common Methods |
|----------|---------|----------------|
| `DcMotor` | Wheels, arms, lifts | `.setPower(-1.0 to 1.0)` |
| `Servo` | Claws, wrists | `.setPosition(0.0 to 1.0)` |
| `CRServo` | Continuous rotation | `.setPower(-1.0 to 1.0)` |
| `DistanceSensor` | Measure distance | `.getDistance(DistanceUnit.INCH)` |
| `ColorSensor` | Detect colors | `.red()`, `.green()`, `.blue()` |
| `TouchSensor` | Detect contact | `.isPressed()` |

---

## Gamepad Controls Reference

### Sticks & Triggers
```java
gamepad1.left_stick_x    // -1.0 to 1.0 (left/right)
gamepad1.left_stick_y    // -1.0 to 1.0 (up/down, FLIPPED!)
gamepad1.right_stick_x   // -1.0 to 1.0 (left/right)
gamepad1.right_stick_y   // -1.0 to 1.0 (up/down, FLIPPED!)
gamepad1.left_trigger    // 0.0 to 1.0
gamepad1.right_trigger   // 0.0 to 1.0
```

### Buttons (return true/false)
```java
gamepad1.a, gamepad1.b, gamepad1.x, gamepad1.y
gamepad1.left_bumper, gamepad1.right_bumper
gamepad1.dpad_up, gamepad1.dpad_down, gamepad1.dpad_left, gamepad1.dpad_right
gamepad1.start, gamepad1.back, gamepad1.guide
```

---

## Common Drive Systems

### Tank Drive
```java
double leftPower = -gamepad1.left_stick_y;
double rightPower = -gamepad1.right_stick_y;
leftMotor.setPower(leftPower);
rightMotor.setPower(rightPower);
```

### Arcade Drive
```java
double forward = -gamepad1.left_stick_y;
double turn = gamepad1.right_stick_x;
leftMotor.setPower(forward + turn);
rightMotor.setPower(forward - turn);
```

### Mecanum Drive
```java
double y = -gamepad1.left_stick_y;  // Forward/backward
double x = gamepad1.left_stick_x;   // Strafe left/right
double rx = gamepad1.right_stick_x; // Rotate

double frontLeftPower = y + x + rx;
double frontRightPower = y - x - rx;
double backLeftPower = y - x + rx;
double backRightPower = y + x - rx;

// Normalize if any power > 1.0
double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
max = Math.max(max, Math.abs(backLeftPower));
max = Math.max(max, Math.abs(backRightPower));

if (max > 1.0) {
    frontLeftPower /= max;
    frontRightPower /= max;
    backLeftPower /= max;
    backRightPower /= max;
}
```

---

## Control Patterns

### Button Toggle
```java
// In class: private boolean clawOpen = false;
// In loop():
if (gamepad1.a && !lastA) {  // Button just pressed
    clawOpen = !clawOpen;
    clawServo.setPosition(clawOpen ? 0.8 : 0.2);
}
lastA = gamepad1.a;
```

### Hold Button for Action
```java
if (gamepad1.right_bumper) {
    armMotor.setPower(0.8);    // Raise arm
} else if (gamepad1.right_trigger > 0.1) {
    armMotor.setPower(-0.8);   // Lower arm
} else {
    armMotor.setPower(0);      // Stop arm
}
```

### Variable Speed Control
```java
double speed = gamepad1.right_trigger * 0.8 + 0.2; // 20% to 100%
motor.setPower(gamepad1.left_stick_y * speed);
```

---

## Autonomous Basics

### Linear OpMode Structure
```java
@Autonomous(name = "Auto Program", group = "Auto")
public class AutoProgram extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Initialize hardware
        DcMotor motor = hardwareMap.get(DcMotor.class, "motor");
        
        waitForStart();  // Wait for start button
        
        // Autonomous actions
        motor.setPower(0.5);
        sleep(2000);         // Wait 2 seconds
        motor.setPower(0);
    }
}
```

### Using Encoders
```java
// Reset and set mode
motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

// Move specific distance
int targetTicks = 1000;  // Calculate based on wheel size
motor.setTargetPosition(targetTicks);
motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
motor.setPower(0.5);

while (opModeIsActive() && motor.isBusy()) {
    // Wait for movement to complete
}
motor.setPower(0);
```

---

## Debugging Tools

### Telemetry
```java
telemetry.addData("Label", "Value: %.2f", variable);
telemetry.addData("Status", "Running");
telemetry.update();  // Don't forget this!
```

### Common Telemetry Info
```java
telemetry.addData("Runtime", getRuntime());
telemetry.addData("Motors", "left: %.2f, right: %.2f", leftPower, rightPower);
telemetry.addData("Encoder", motor.getCurrentPosition());
telemetry.addData("Sensor", "Distance: %.1f inches", distanceSensor.getDistance(DistanceUnit.INCH));
```

---

## Quick Troubleshooting

| Problem | Most Likely Cause | Solution |
|---------|-------------------|----------|
| Robot doesn't move | Hardware names don't match | Check phone config vs code |
| Robot moves backward | Motor direction wrong | Change `.setDirection()` |
| Jerky movement | Power changes too quickly | Add gradual acceleration |
| Servo doesn't work | Position out of range | Use values 0.0 to 1.0 only |
| Encoder reads 0 | Wrong run mode | Use `RUN_USING_ENCODER` |
| Program won't start | Compilation error | Check for missing semicolons |

---

## Essential Calculations

### Convert Inches to Encoder Ticks
```java
double COUNTS_PER_MOTOR_REV = 1440;    // Example for REV Core Hex
double DRIVE_GEAR_REDUCTION = 2.0;     // Gear ratio
double WHEEL_DIAMETER_INCHES = 4.0;    // Measure your wheels
double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / 
                         (WHEEL_DIAMETER_INCHES * 3.1415);

int targetTicks = (int)(inches * COUNTS_PER_INCH);
```

### Limit Values to Range
```java
// Keep value between min and max
value = Math.max(min, Math.min(max, value));

// Example: keep motor power between -1 and 1
motorPower = Math.max(-1.0, Math.min(1.0, motorPower));
```

---

## OpMode Types

| Type | Use Case | Key Features |
|------|----------|--------------|
| `OpMode` | TeleOp, complex auto | `init()` and `loop()` methods |
| `LinearOpMode` | Simple autonomous | Sequential code with `waitForStart()` |

### Annotations
```java
@TeleOp(name = "Driver Control", group = "TeleOp")
@Autonomous(name = "Auto Program", group = "Autonomous")  
@Disabled  // Hides from phone menu
```

---

## Best Practices

### Code Organization
- Use meaningful variable names (`frontLeftMotor` not `m1`)
- Add comments explaining complex logic
- Group related code together
- Keep methods short and focused

### Safety First
- Always initialize hardware in `init()`
- Set safe default positions for servos
- Add emergency stops (both bumpers = stop all)
- Test at low power first

### Performance Tips
- Avoid `sleep()` in TeleOp loop methods
- Minimize telemetry output (impacts performance)
- Cache frequently used calculations
- Use appropriate data types (`int` vs `double`)

---

## Getting Help

### Error Messages
- **Syntax Error:** Missing semicolon, bracket, or quote
- **NullPointerException:** Hardware not initialized
- **IllegalArgumentException:** Value out of range (servo position, etc.)

### Resources
- **FTC Docs:** ftc-docs.firstinspires.org
- **FTC Discord:** discord.gg/first-programs
- **Example Code:** github.com/FIRST-Tech-Challenge/FtcRobotController

### Common File Locations
- **Your Code:** `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`
- **Hardware Config:** Saved on Robot Controller phone
- **Logs:** Robot Controller → Settings → View Logs

---
