package org.firstinspires.ftc.teamcode.controlled;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.GameMap;
import org.firstinspires.ftc.teamcode.util.Intake;
import org.firstinspires.ftc.teamcode.util.MechanismActions;
import org.firstinspires.ftc.teamcode.util.Outtake;

/**
 * TODO:
 *  give pose to next OpMode/take pose from previous
 *  assisted aim if necessary
 *  dpad mode if necessary
 */
@TeleOp(name = "Two Controller")
public class TwoControlled extends LinearOpMode{
    public void runOpMode(){
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0.0, 0.0, 0.0));
        Intake intake = new Intake(
                hardwareMap.get(Servo.class, "intake_servo_a1"),
                hardwareMap.get(Servo.class, "intake_servo_a2"),
                hardwareMap.get(Servo.class, "intake_servo_b"),
                hardwareMap.get(DcMotor.class, "intake_motor")
        );
        Outtake outtake;
        outtake = new Outtake(
                hardwareMap.get(Servo.class, "outtake_servo"),
                hardwareMap.get(DcMotor.class, "outtake_slide_motor")
        );
        MechanismActions actions = new MechanismActions(intake,outtake,this);
        Thread t = new Thread(() -> {
            while (opModeIsActive()){
                drive.setDrivePowers(new PoseVelocity2d(new Vector2d(-gamepad2.left_stick_y, -gamepad2.left_stick_x), -gamepad2.right_stick_x));
            }
        });
        waitForStart();
        t.start();
        while (opModeIsActive()){
            if (gamepad1.left_bumper){
                Actions.runBlocking(actions.ReadyGrabAction(gamepad1.left_trigger * (GameMap.MaxIntakeDistance - GameMap.MinIntakeDistance) + GameMap.MinIntakeDistance));
                intake.grab();
            } else if (gamepad1.right_bumper){
                Actions.runBlocking(actions.FullTransferAction());
            }
            if (gamepad1.dpad_up){
                intake.grab();
            } else if (gamepad1.dpad_down){
                intake.release();
            } else if (gamepad1.dpad_left) {
                intake.stop();
            }
            if (gamepad1.y){
                Actions.runBlocking(actions.OuttakeSlideUpAction());
            } else if (gamepad1.a){
                Actions.runBlocking(actions.EndScoring());
            } else if (gamepad1.b){
                outtake.open();
            }
        }
    }
}
