package org.firstinspires.ftc.teamcode.util;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
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
            boolean finished = (outtake.linear_slide.extendToIter(4850,50));
            if (finished){
                outtake.linear_slide.stop();
            }
            return !finished;
        }
    }
    public class OuttakeSlideDownAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            boolean finished = (outtake.linear_slide.extendToIter(0, 50));
            if (finished) {
                outtake.linear_slide.stop();
            }
            return !finished;
        }
    }
    public class OpenGateAction implements Action{
        double start_time = -1;
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (start_time == -1){
                start_time = System.currentTimeMillis();
            }
            outtake.open();
            return ((System.currentTimeMillis() - start_time) < 1000);
        }
    }
    public class CloseGateAction implements Action{
        double start_time = -1;
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (start_time == -1){
                start_time = System.currentTimeMillis();
            }
            outtake.close();
            return ((System.currentTimeMillis() - start_time) < 1000);
        }
    }
    public Action ReadyGrabAction(double to){
        return new ParallelAction(new HSlideToAction(to), new IntakeDownAction());
    }
    public class IntakeReleaseAction implements Action{
        double start_time = -1;
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (start_time == -1){
                start_time = System.currentTimeMillis();
            }
            intake.release();
            return (System.currentTimeMillis() - start_time) < 1000;
        }
    }

    public class HSlideToAction implements Action{
        double to;
        public HSlideToAction(double to){
            super();
            this.to = to;
        }
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            boolean finished = (intake.linear_slide.extendToIter(to, 50));
            if (finished){
                intake.linear_slide.stop();
            }
            return !finished;
        }
    }
    public class HSlideInAction implements Action{
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
           boolean finished = (intake.linear_slide.extendToIter(0, 10));
           if (finished){
               intake.linear_slide.stop();
           }
           return !finished;
        }
    }
    public class IntakeDownAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            boolean out = (intake.moveDown() < 0.01);
            return !out;
        }
    }
    public class IntakeUpAction implements Action{
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            return !(intake.moveUp() < 0.01);
        }
    }
    public class IntakeGrabAction implements Action{
        double start_time = -1;
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (start_time == -1){
                start_time = System.currentTimeMillis();
            }
            intake.grab();
            return ((System.currentTimeMillis() - start_time) < 1000);
        }
    }
    public class IntakeHoldAction implements Action{
        double start_time;
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            intake.hold();
            return false;
        }
    }
    public Action ReadyTransferAction(){
        return new ParallelAction(new OuttakeSlideDownAction(), new CloseGateAction(), new HSlideInAction(), new IntakeUpAction(), new IntakeHoldAction());
    }
    public Action FullTransferAction(){
        return new SequentialAction(ReadyTransferAction(), new IntakeReleaseAction());
    }
    public Action FullGrabAction(){
        return new SequentialAction(ReadyGrabAction(1000), new IntakeGrabAction());
    }
    public Action FullGrabAction(double to){
        return new SequentialAction(ReadyGrabAction(to), new IntakeGrabAction());
    }
    public Action FullScoreAction(){
        return new SequentialAction( new OuttakeSlideUpAction(),new OpenGateAction(), new CloseGateAction(), new OuttakeSlideDownAction());
    }
    public Action OuttakeSlideUpAction(){
        return new OuttakeSlideUpAction();
    }
    public Action EndScoring(){
        return new ParallelAction(new OuttakeSlideDownAction(), new SequentialAction(new SleepAction(0.3), new CloseGateAction()));
    }
}
