package org.firstinspires.ftc.teamcode.util.linearslides;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class OuttakeSlide extends LinearSlide {
    private static final double MAX_EXTEND = 5050;
    private static final double MIN_EXTEND = 0;

    /**
     * Class for using the Outtake slide
     * @param hwmap the hardware map, used to find the motor
     */
    public OuttakeSlide(HardwareMap hwmap) {
        super(hwmap.get(DcMotor.class, "outtake_slide_motor"), MIN_EXTEND, MAX_EXTEND, 0, 50);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public double powerFunction(){
        return Math.signum(target_pos - getPosition());
    }

    @Override
    public void stop(){
        if (getPosition() > 2000) {
            motor.setPower(0.1);
        } else {
            motor.setPower(0.0);
        }
    }

    public void moveDown(){
        this.goTo(0, 50);
    }

    public void moveUp(){
        this.goTo(5000, 50);
    }
}
