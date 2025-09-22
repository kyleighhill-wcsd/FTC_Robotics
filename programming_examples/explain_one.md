# FTC Java Programming Guide for Students

## Overview
This guide explains a basic FTC (FIRST Tech Challenge) robot control program written in Java. We'll break it down for students with different programming backgrounds.

---

## For Students New to Programming

### What is Java?
Java is a programming language - it's like giving instructions to a computer, but instead of using English, we use special words and symbols that the computer understands. Think of it like learning a new language to talk to robots!

### Key Programming Concepts

#### 1. Variables - Information Storage Boxes
Variables are like labeled boxes that hold information:
```java
double drivePower = 0.8;    // A box labeled "drivePower" containing 0.8
```
- `double` means this box can hold decimal numbers
- `drivePower` is the name of our box
- `0.8` is the value we put inside (80% power)

#### 2. Methods - Recipe Instructions
Methods are like recipes that tell the robot what steps to follow:
- `init()` - The "setup recipe" that runs once when you press INIT
- `loop()` - The "main recipe" that runs over and over while the robot operates

#### 3. Comments - Notes for Humans
Text after `//` are comments - notes that help humans understand the code. The robot completely ignores these!
```java
// This is a comment - it explains what the code does
double speed = 0.5;  // Set speed to 50%
```

---

## For Students New to Java (with some programming experience)

### Java-Specific Concepts

#### 1. Classes and Objects
```java
public class BasicTeleOp extends OpMode
```
- Our program is a **class** called `BasicTeleOp`
- It **extends** `OpMode`, meaning it inherits abilities from a parent class
- Think of it like getting superpowers from a superhero parent!

#### 2. Import Statements
```java
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
```
- These bring in pre-written code we can use
- Like borrowing tools from a toolbox instead of making them ourselves
- FTC provides these tools specifically for robot programming

#### 3. Annotations
```java
@TeleOp(name = "Basic Robot Control", group = "TeleOp")
@Override
```
- `@TeleOp` tells the FTC system this is a driver-controlled program
- `@Override` means we're replacing a method from the parent class with our own version

#### 4. Data Types
- `DcMotor` - Represents a motor that can spin forwards/backwards
- `Servo` - Represents a servo that moves to specific positions
- `double` - Numbers with decimal points (like 0.8, -1.5)

---

## Program Structure Breakdown

### 1. Hardware Declaration Section
```java
private DcMotor frontLeftMotor;
private DcMotor frontRightMotor;
// ... more hardware components
```

**What's happening:** We're creating variables that will represent the physical parts of our robot.

**For beginners:** Think of these as name tags for robot parts. We're not building the robot here, just preparing labels for the parts that already exist.

**Key concept:** `private` means these variables belong only to this class - other parts of the program can't mess with them.

### 2. Configuration Variables
```java
private double drivePower = 0.8;
private double armPower = 0.5;
```

**What's happening:** We're setting up adjustable values that control robot behavior.

**Why this matters:** Instead of putting numbers directly in our code, we put them in variables. This makes it easy to adjust robot behavior by changing one number instead of hunting through the entire program.

### 3. Initialization Method (init)
```java
@Override
public void init() {
    // Connect code to hardware
    frontLeftMotor = hardwareMap.get(DcMotor.class, "front_left");
```

**What's happening:** This method runs exactly once when you press INIT on the driver station phone.

**Step by step:**
1. **Hardware Mapping:** `hardwareMap.get()` connects our code variables to the actual robot parts
2. **Direction Setting:** Some motors might be mounted backwards, so we tell them which way is "forward"
3. **Safety Setup:** Make sure everything starts in a safe state (motors stopped, servo in middle position)

**Critical point:** The names in quotes (like "front_left") must exactly match what you named the hardware when configuring the robot phone.

### 4. Main Control Loop
```java
@Override
public void loop() {
    // This runs about 50 times per second!
```

**What's happening:** This method runs continuously while the robot operates - about 50 times every second!

**The loop does four main things:**
1. **Read Input:** Get controller stick and button positions
2. **Calculate:** Figure out what the robot should do based on input
3. **Act:** Send commands to motors and servos
4. **Display:** Show information on the phone screen

---

## Control System Explained

### Reading Controller Input
```java
double leftStickY = -gamepad1.left_stick_y;   // Forward/backward
double leftStickX = gamepad1.left_stick_x;    // Left/right strafe
double rightStickX = gamepad1.right_stick_x;  // Rotation
```

**For beginners:** The controller has joysticks that can move in different directions. When you move a stick, it creates a number between -1.0 and +1.0.

**Why the negative sign:** The Y-axis on controllers is flipped - pushing forward gives a negative number, but we want forward to be positive for our robot.

### Mecanum Drive Calculation
```java
double frontLeftPower = leftStickY + leftStickX + rightStickX;
double frontRightPower = leftStickY - leftStickX - rightStickX;
// ... and so on
```

**What's happening:** This is the math that lets a robot with mecanum wheels move in any direction and rotate at the same time.

**For beginners:** Don't worry about understanding this math yet. Just know that it takes the three joystick inputs and figures out how fast each wheel should spin to make the robot move the way you want.

**For advanced students:** This is vector addition - we're combining the desired forward/back motion, side-to-side motion, and rotational motion into individual wheel speeds.

### Power Normalization
```java
double maxPower = Math.max(Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                          Math.max(Math.abs(backLeftPower), Math.abs(backRightPower)));
if (maxPower > 1.0) {
    frontLeftPower /= maxPower;
    // ... divide all powers by maxPower
}
```

**Problem solved:** Sometimes our calculations might tell a motor to run at 150% power, but motors can only go up to 100%.

**Solution:** If any wheel would exceed 100%, we proportionally reduce all wheel powers so the strongest one becomes exactly 100%. This keeps the robot moving in the right direction, just slower.

### Button Controls
```java
if (gamepad1.right_bumper) {
    armMotor.setPower(armPower);
} else if (gamepad1.right_trigger > 0.1) {
    armMotor.setPower(-armPower);
} else {
    armMotor.setPower(0);
}
```

**What's happening:** We check if certain buttons are pressed and respond accordingly.

**Logic flow:**
1. If right bumper is pressed → move arm up
2. Else if right trigger is pressed → move arm down  
3. Else → stop the arm

**Note:** We check `right_trigger > 0.1` instead of just `right_trigger` because triggers can have tiny values even when not pressed.

---

## Key Programming Concepts Demonstrated

### 1. Hardware Abstraction
Our code doesn't need to know the electrical details of how motors work - it just says "spin at 80% power" and the FTC system handles the rest.

### 2. Input Processing
We take raw controller input and convert it into meaningful robot actions.

### 3. Mathematical Calculations
Converting joystick positions into wheel speeds requires math, but the computer does it instantly.

### 4. Conditional Logic
`if/else` statements let the robot make decisions based on controller input.

### 5. Real-time Systems
The loop runs 50 times per second, constantly reading input and updating robot behavior.

### 6. Debugging and Feedback
```java
telemetry.addData("Drive Power", "%.2f", drivePower);
telemetry.update();
```
Telemetry displays information on the phone screen, helping us understand what the robot is thinking.

---

## Hands-On Learning Activities

### For Absolute Beginners
1. **Modify Power Values:** Change `drivePower` from 0.8 to 0.5 and see how it affects robot speed
2. **Add Comments:** Write your own comments explaining what different parts do
3. **Experiment with Telemetry:** Add new telemetry lines to display different information

### For Programming Newcomers
1. **Button Mapping:** Change which buttons control the arm or claw
2. **Add New Controls:** Add a button that toggles between fast and slow driving modes
3. **Safety Features:** Add code that stops all motors if both bumpers are pressed

### For Java Beginners
1. **Create Methods:** Move the drive code into its own method called `handleDriving()`
2. **Add Variables:** Create variables to track robot state (like whether the claw is open)
3. **Implement Features:** Add a toggle for the claw instead of separate open/close buttons

---

## Common Mistakes and Troubleshooting

### Hardware Configuration Errors
**Problem:** Robot doesn't respond to controls
**Likely cause:** Hardware names in code don't match phone configuration
**Solution:** Double-check that "front_left" in code matches "front_left" in phone config

### Direction Problems
**Problem:** Robot moves backwards when you push forward
**Solution:** Change `DcMotor.Direction.FORWARD` to `DcMotor.Direction.REVERSE` for affected motors

### Servo Range Issues
**Problem:** Claw doesn't open/close properly
**Solution:** Adjust `clawOpenPosition` and `clawClosedPosition` values (must be between 0.0 and 1.0)

### Performance Issues
**Problem:** Robot responds slowly or jerkily
**Possible causes:** 
- Loop is taking too long (avoid delays in loop!)
- Too much telemetry output
- Hardware connection problems

---

## Next Steps

### Once Students Master the Basics:
1. **Autonomous Programming:** Learn to make robots move without driver input
2. **Sensors:** Add encoders, gyroscopes, and cameras
3. **Advanced Control:** PID controllers, state machines, and path following
4. **Team Collaboration:** Version control with Git, code organization

### Programming Skills to Develop:
- **Object-Oriented Programming:** Creating your own classes
- **Algorithm Design:** More efficient ways to solve problems  
- **Debugging:** Systematic approaches to finding and fixing bugs
- **Documentation:** Writing clear comments and user guides

---

## Resources for Further Learning

- **FTC Documentation:** Official guides and tutorials
- **Java Basics:** Oracle's Java tutorials
- **FTC Community:** Forums and Discord channels for help
- **GitHub:** Look at other teams' code for inspiration

Remember: Programming is learned by doing! Start with small changes, test them, and gradually build up to more complex features.