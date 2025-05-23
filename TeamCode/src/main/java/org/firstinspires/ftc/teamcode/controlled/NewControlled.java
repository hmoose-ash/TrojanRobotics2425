package org.firstinspires.ftc.teamcode.controlled;

import static java.lang.Math.PI;
import static java.lang.Math.min;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.util.Intake;
import org.firstinspires.ftc.teamcode.util.Outtake;
import org.firstinspires.ftc.teamcode.util.PoseStorer;
import org.firstinspires.ftc.teamcode.util.customactions.ForeverAction;
import org.firstinspires.ftc.teamcode.util.customactions.TriggerAction;
import org.firstinspires.ftc.teamcode.util.linearslides.IntakeSlide;

@TeleOp(name = "New Controlled")
public class NewControlled extends LinearOpMode {
    FtcDashboard dash = FtcDashboard.getInstance();
    double last_time = 0;
    public void runOpMode(){
        MecanumDrive drive = new MecanumDrive(hardwareMap, PoseStorer.pose);
        Intake intake;
        intake = new Intake(hardwareMap);
        Outtake outtake;
        intake.claw.toReadyGrabPos();
        outtake = new Outtake(hardwareMap);

        InstantAction intake_update = new InstantAction(() -> {
            if (Math.abs(gamepad1.left_stick_y) > 0.5) {
                intake.linear_slide.goTo(
                        intake.linear_slide.trimTicks(intake.linear_slide.getPosition() - 100 * Math.signum(gamepad1.left_stick_y)),
                        50
                );
                intake.linear_slide.setMotorPower(-gamepad1.left_stick_y * 2 + Math.signum(gamepad1.left_stick_y));
            } else if (gamepad1.dpad_up) {
                intake.linear_slide.goTo(
                        intake.linear_slide.trimTicks(intake.linear_slide.getPosition() + 200 * (last_time - time)),
                        50
                );
                intake.linear_slide.setMotorPower(0.1);
            } else if (gamepad1.dpad_down) {
                intake.linear_slide.goTo(
                        intake.linear_slide.trimTicks(intake.linear_slide.getPosition() - 200 * (last_time - time)),
                        50
                );
                intake.linear_slide.setMotorPower(-0.1);
            } else {
                if (gamepad1.left_stick_button) {
                    intake.linear_slide.moveOut();
                }
                intake.linear_slide.movementLoop();
            }
        });

        InstantAction movement = new InstantAction(() -> {
            if (intake.linear_slide.getPosition() > 200 || gamepad1.right_stick_button) {
                drive.setDrivePowers(new PoseVelocity2d(new Vector2d(-gamepad1.right_stick_y/3, -gamepad1.left_stick_x/3), -gamepad1.right_stick_x/3));
            } else {
                drive.setDrivePowers(new PoseVelocity2d(new Vector2d(-gamepad1.right_stick_y, -gamepad1.right_stick_x), gamepad1.left_stick_x));
            }
            drive.updatePoseEstimate();
        }
        );
        boolean old_a = false;
        boolean old_b = false;
        waitForStart();
        while (opModeIsActive()){
            double turret_angle = 0;
            outtake.backgroundIter();
            telemetry.addData("loop time after outtake", (time - last_time) * 1000);
            intake_update.getF().run();
            telemetry.addData("loop time after intake+outtake", (time - last_time) * 1000);
            movement.getF().run();
            telemetry.addData("loop time after intake+outtake+movement", (time - last_time) * 1000);
            if (gamepad1.left_bumper){
                Actions.runBlocking(
                    new RaceAction(
                        new ForeverAction(movement),
                        new ForeverAction(outtake::backgroundIter),
                        new SequentialAction(
                            intake.fullTransferAction(),
                            new InstantAction(intake.claw::toReadyGrabPos)
                        )
                    )
                );
            }
            if (gamepad1.right_bumper) {
                Actions.runBlocking(
                    new RaceAction(
                        new ForeverAction(movement),
                        new ForeverAction(outtake::backgroundIter),
                        intake.pickUpAction()
                    )
                );

            }
            if (gamepad1.x) {
                outtake.slide.up();
            } else if (gamepad1.y) {
                outtake.slide.down();
            }
            if (gamepad1.left_trigger > 0.4) {
                outtake.slide.down();
            } else if (gamepad1.right_trigger > 0.4) {
                outtake.slide.up();
            }
            if (gamepad1.a && !old_a){
                outtake.toggleBucket();
            }
            if (gamepad1.b && !old_b){
                intake.claw.toggleGrab();
            }
            if (gamepad1.dpad_left) {
                turret_angle += (time-last_time);
                intake.claw.rotate(turret_angle);
            } else if (gamepad1.dpad_right){
                turret_angle -= (time-last_time);
                intake.claw.rotate(turret_angle);
            }
            old_a = gamepad1.a;
            old_b = gamepad1.b;
            telemetry.addData("loop time (ms))", (time - last_time) * 1000);
            telemetry.addData("turret angle (deg)", turret_angle/PI * 180);
            last_time = time;
            telemetry.update();
        }
    }
}
