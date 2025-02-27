package org.firstinspires.ftc.teamcode.util;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.DashboardCore;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MechanismActions {
    Intake intake;
    Outtake outtake;
    LinearOpMode opMode;
    public MechanismActions(Intake intake, Outtake outtake, LinearOpMode opMode){
        this.intake = intake;
        this.outtake = outtake;
        this.opMode = opMode;
    }
    public class OuttakeSlideUpAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            outtake.up();
            return !outtake.linear_slide.within_error;
        }
    }
    public class OuttakeSlideDownAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            outtake.down();
            return !outtake.linear_slide.within_error;
        }
    }

    public Action OpenGateAction(){
        return new ParallelAction(new SleepAction(1), new InstantAction(() -> outtake.open()));
    }
    public Action CloseGateAction(){
        return new InstantAction(() -> outtake.close());
    }
    public Action ReadyGrabAction(double distance_in){
        return new SequentialAction( new ParallelAction( new HSlideToAction(distance_in), new IntakeDownAction()), GrabSpinAction());
    }
    public Action IntakeReleaseAction(){
        return new ParallelAction(new SleepAction(1), new InstantAction(() -> intake.release()));
    }

    public class HSlideToAction implements Action{
        double distance_in;
        public HSlideToAction(double distance_in){
            super();
            this.distance_in = distance_in;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.slideTo(intake.linear_slide.inToTicks(distance_in));
            return !intake.linear_slide.within_error;
        }
    }
    public class HSlideInAction implements Action {
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.slideIn();
            return !intake.linear_slide.within_error;
        }
    }
    public class IntakeDownAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            boolean out = (intake.moveDown() < 0.01);
            return !out;
        }
    }
    public Action AsyncAction(Action action){
        return new InstantAction(
                () -> {
                    Thread t = new Thread(()->{
                        FtcDashboard dash = FtcDashboard.getInstance();
                        while (true){
                            TelemetryPacket packet = new TelemetryPacket();
                            action.run(packet);
                            dash.sendTelemetryPacket(packet);
                        }
                    });
                }
        );
    }
    public Action IntakeUpAction(){
        return new ParallelAction(new SleepAction(1.2), new InstantAction(() -> intake.moveUp()));
    }
    public Action IntakeGrabAction(){
        return new InstantAction(() -> intake.grab());
    }
    public Action IntakeHoldAction(){
        return new InstantAction(() -> intake.hold());
    }
    public Action ReadyTransferAction(){
        return new ParallelAction(new OuttakeSlideDownAction(), new HSlideInAction(), IntakeHoldAction(), IntakeUpAction());
    }
    public Action FullTransferAction(){
        return new SequentialAction(ReadyTransferAction(), IntakeReleaseAction());
    }
    public Action FullGrabAction(){
        return new SequentialAction(ReadyGrabAction(0), IntakeGrabAction());
    }
    public Action FullGrabAction(double distance_in){
        return new SequentialAction(ReadyGrabAction(distance_in), new ParallelAction(IntakeGrabAction()));
    }
    public Action ScoreAction(){
        return new SequentialAction( new OuttakeSlideUpAction(), OpenGateAction(), AsyncAction(OuttakeSlideDownAction()));
    }
    public Action FullScoreAction(){
        return new SequentialAction( new OuttakeSlideUpAction(), OpenGateAction(), OuttakeSlideDownAction());
    }
    public Action OuttakeSlideUpAction(){
        return new OuttakeSlideUpAction();
    }
    public Action OuttakeSlideDownAction(){
        return new ParallelAction(new OuttakeSlideDownAction(), new SequentialAction(new SleepAction(0.3), CloseGateAction()));
    }
    public Action GrabSpinAction() {
        return IntakeGrabAction();
    }
}
