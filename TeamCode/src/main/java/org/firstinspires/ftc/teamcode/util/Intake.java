package org.firstinspires.ftc.teamcode.util;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.util.linearslides.IntakeSlide;
import org.firstinspires.ftc.teamcode.util.linearslides.LinearSlide;

import java.util.Objects;

public class Intake {

    //MechanismActions actions = new MechanismActions();
    Servo servo0;
    Servo servo1;
    Servo servo2;
    Servo servo3;
    public IntakeSlide linear_slide;

    public String intake_state = "closed";

    public Intake(HardwareMap hwmap){
        this.servo0 = hwmap.get(Servo.class, "cs0");
        this.servo1 = hwmap.get(Servo.class, "cs1");
        this.servo2 = hwmap.get(Servo.class, "cs2");
        this.servo3 = hwmap.get(Servo.class, "cs3");
        this.linear_slide = new IntakeSlide(hwmap.get(DcMotor.class, "cm0"));
    }

    public void grab() {
        if (Objects.equals(intake_state, "open")) {
            if (servo0.getPosition() == 0) {
                servo0.setPosition(1);
            } else {
                servo0.setPosition(0);
            }
        }
    }

    public void longIntake() {
        intake_state = "open";

        extendSlide();
        openArm();
    }

    public void shortIntake() {
        intake_state = "open";

        retractSlide();
        openArm();
    }

    public void close() {
        intake_state = "closed";

        closeArm();
        retractSlide();
    }

    public void moveWristRight() {
        if (Objects.equals(intake_state, "open")) {
            servo1.setPosition(servo1.getPosition()+0.003);
        }
    }

    public void moveWristLeft() {
        if (Objects.equals(intake_state, "open")) {
            servo1.setPosition(servo1.getPosition()-0.003);
        }
    }

    private void openArm() {
        if (Objects.equals(intake_state, "open")) {
            servo0.setPosition(0);
        } else {
            servo0.setPosition(1);
        }
        servo1.setPosition(0.57);
        servo2.setPosition(0.87);
        servo3.setPosition(0.6);
    }

    private void closeArm() {
        servo0.setPosition(0);
        servo1.setPosition(0.57);
        servo2.setPosition(0.2);
        servo3.setPosition(0);
    }

    public void transferPos() {
        intake_state = "transfer";

        linear_slide.goTo(500, 50);
        servo0.setPosition(0);
        servo1.setPosition(0.57);
        servo2.setPosition(0.2);
        servo3.setPosition(0.15);
    }

    public void activateClaw() {
        if (servo0.getPosition() == 0) {
            servo0.setPosition(1);
        } else {
            servo0.setPosition(0);
        }
    }

    private void retractSlide() {
        linear_slide.goTo(0, 50);
    }
    private void extendSlide() {
        linear_slide.goTo(1100, 50);
    }
}
