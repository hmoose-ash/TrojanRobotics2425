package com.example.meepmeeptesting;

import static java.lang.Math.PI;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MeepMeepTesting {
    public static void main(String[] args) {
        Vector2d score_position =
                GameMap.NetRedCorner.plus(new Vector2d(9.5,9.5)).plus(
                        Rotation2d.fromDouble(PI/4).times(new Vector2d(GameMap.OuttakeDistance, 0))
                );
        final Pose2d start_pose = new Pose2d(GameMap.NetRedCorner.plus(new Vector2d(24.5 + GameMap.RobotWidth / 2, GameMap.RobotLength / 2)), PI / 2);
        final Pose2d score_pose = new Pose2d(GameMap.NetRedCorner.plus(new Vector2d(17.5, 17.5)), PI / 4);
        final Vector2d park_position = GameMap.ObservationRedCorner.plus(new Vector2d(-11.25, GameMap.RobotLength / 2 + 1));
        final Vector2d neutral_spike_mark_position = GameMap.SpikeMarkNeutralLeftInner.minus(new Vector2d(0, GameMap.MinIntakeDistance + 1));
        final Vector2d InnerDistance = GameMap.SpikeMarkNeutralLeftInner.minus(neutral_spike_mark_position);
        final Vector2d CenterDistance = GameMap.SpikeMarkNeutralLeftCenter.minus(neutral_spike_mark_position);
        final Vector2d OuterDistance = GameMap.SpikeMarkNeutralLeftOuter.minus(neutral_spike_mark_position);
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity robot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(50, 50, PI, PI, 15)
                .setDimensions(GameMap.RobotWidth, GameMap.RobotLength)
                .build();
        Action action = robot.getDrive().actionBuilder(start_pose)
                .strafeToSplineHeading(score_pose.position, score_pose.heading)
//                .stopAndAdd(new ParallelAction(ma.FullScoreAction(), ma.ReadyGrabAction(InnerDistance.norm())))
                .setTangent(0)
                .strafeToSplineHeading(neutral_spike_mark_position, InnerDistance.angleCast().toDouble())
//                .stopAndAdd(ma.GrabSpinAction())
                .setTangent(0)
                .strafeTo(neutral_spike_mark_position.plus(InnerDistance.div(InnerDistance.norm()).angleCast().times(new Vector2d(4, 0))))
//                .stopAndAdd(ma.FullTransferAction())
                .setTangent(0)
                .strafeToSplineHeading(score_pose.position, score_pose.heading)
//                .stopAndAdd(new ParallelAction(ma.FullScoreAction(), ma.ReadyGrabAction(CenterDistance.norm() - 2)))
                .setTangent(0)
                .strafeToSplineHeading(neutral_spike_mark_position, CenterDistance.angleCast().toDouble())
//                .stopAndAdd(ma.GrabSpinAction())
                .setTangent(0)
                .strafeTo(neutral_spike_mark_position.plus(CenterDistance.div(CenterDistance.norm()).angleCast().times(new Vector2d(4, 0))))
//                .stopAndAdd(ma.FullTransferAction())
                .setTangent(0)
                .strafeToSplineHeading(score_pose.position, score_pose.heading)
//                .stopAndAdd(new ParallelAction(ma.FullScoreAction(), ma.ReadyGrabAction(OuterDistance.norm() - 2)))
                .setTangent(0)
                .strafeToSplineHeading(neutral_spike_mark_position, OuterDistance.angleCast().toDouble())
//                .stopAndAdd(ma.GrabSpinAction())
                .setTangent(0)
                .strafeTo(neutral_spike_mark_position.plus(OuterDistance.div(OuterDistance.norm()).angleCast().times(new Vector2d(4, 0))))
//                .stopAndAdd(ma.FullTransferAction())
                .setTangent(0)
                .strafeToSplineHeading(score_pose.position, score_pose.heading)
//                .stopAndAdd(ma.FullScoreAction())
                .setTangent(0)
                .strafeToSplineHeading(park_position, PI/2)
                .build();
        robot.runAction(action);
        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .addEntity(robot)
                .start();
    }
    public static Action redNetZoneToRedSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2)
                .build();
    }
    public static Action redNetZoneToLeftNeutralSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2)
                .build();
    }
    public static Action redNetZoneToRightNeutralSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.NetRedCorner.times(0.666).minus(new Vector2d(-40, 0)), 0)
                .splineTo(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 11.25)), PI/2)
                .build();
    }
    public static Action redNetZoneToRedAscentZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.NetRedCorner.times(0.5), -PI/4)
                .splineTo(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25,0)), PI/2)
                .build();
    }
    public static Action redNetZoneToRedObservationZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.ObservationRedCorner.plus(new Vector2d(-35.25, 23.5)), -1*PI/4)
                .build();
    }
    public static Action redNetZoneToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action redSpikeMarksToLeftNeutralSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2)
                .build();
    }
    public static Action redSpikeMarksToRightNeutralSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2)
                .build();
    }
    public static Action redSpikeMarksToRedAscentZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkRedCenter.minus(new Vector2d(64, 13)), PI/2)
                .splineTo(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)),PI/2)
                .build();
    }
    public static Action redSpikeMarksToRedObservationZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4)
                .build();
    }
    public static Action redSpikeMarksToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action leftNeutralSpikeMarksToRightNeutralSpikeMarks(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralLeftCenter.plus(new Vector2d(62, -11)), PI/2)
                .splineTo(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), 0)
                .build();
    }
    public static Action leftNeutralSpikeMarksToRedAscentZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2))
                .splineTo(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0)
                .build();
    }
    public static Action leftNeutralSpikeMarksToRedObservationZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2))
                .strafeToSplineHeading(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4)
                .build();
    }
    public static Action leftNeutralSpikeMarksToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedAscentZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .splineTo(new Vector2d(25, -37), -PI)
                .strafeToSplineHeading(new Vector2d(-10, -37), -PI)
                .splineTo(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedObservationZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .strafeToSplineHeading(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .strafeToSplineHeading(new Vector2d(25, -40), -PI)
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action redAscentZoneToRedObservationZone(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0))
                .strafeToSplineHeading(new Vector2d(-26, -30), -1*PI/8)
                .strafeToSplineHeading(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4)
                .build();
    }
    public static Action redAscentZoneToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0))
                .strafeToSplineHeading(new Vector2d(-26, -40), -1*PI/8)
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action redObservationZoneToRedSubmersibleEdge(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4))
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }
    public static Action redNetZoneToRightNeutralSpikeMarksBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.NetRedCorner.times(0.666), -3*PI/4))
                .strafeToSplineHeading(GameMap.NetRedCorner.times(0.666).minus(new Vector2d(-40, 0)), PI/4)
                .splineTo(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 11.25)), PI/4)
                .build();
    }
    public static Action redSpikeMarksToRightNeutralSpikeMarksBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkRedCenter.minus(new Vector2d(1, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkRedCenter.minus(new Vector2d(35.25, 11)), PI/4+0.15)
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2)
                .build();
    }
    public static Action leftNeutralSpikeMarksToRedAscentZoneBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-2, 11)), PI/2))
                .strafeToSplineHeading(GameMap.SpikeMarkNeutralLeftCenter.minus(new Vector2d(-22, 11)), PI/2)
                .strafeToSplineHeading(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedAscentZoneBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .strafeToSplineHeading(new Vector2d(25, -37), -PI)
                .strafeToSplineHeading(new Vector2d(-10, -37), -PI)
                .splineTo(GameMap.AscentZoneEdgeRed.minus(new Vector2d(11.25, 0)), 0)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedObservationZoneBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .strafeToSplineHeading(new Vector2d(30, -24), PI/2)
                .strafeToSplineHeading(GameMap.ObservationRedCorner.minus(new Vector2d(35.25, -23.5)), -1*PI/4)
                .build();
    }
    public static Action rightNeutralSpikeMarksToRedSubmersibleEdgeBlocked(RoadRunnerBotEntity robot){
        return robot.getDrive().actionBuilder(new Pose2d(GameMap.SpikeMarkNeutralRightCenter.minus(new Vector2d(1, 13)), PI/2))
                .strafeToSplineHeading(new Vector2d(25, -40), -PI)
                .strafeToSplineHeading(GameMap.SubmersibleRedEdge.plus(new Vector2d(0, -11.25)), PI/2)
                .build();
    }

}