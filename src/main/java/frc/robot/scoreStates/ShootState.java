package frc.robot.scoreStates;

import com.pathplanner.lib.auto.AutoBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Constants;
import frc.robot.PoseEstimation;
import frc.robot.subsystems.hood.Hood;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterConstants;
import frc.robot.swerve.SwerveDrive;
import java.util.List;
import java.util.Set;
import lib.Utils;

public class ShootState implements ScoreState {
    private static Shooter shooter;
    private static Hood hood;
    private Translation2d speakerPose;

    public ShootState() {
        shooter = Shooter.getInstance();
        hood = Hood.getInstance();
    }

    @Override
    public Command driveToClosestOptimalPoint() {
        return Commands.defer(
                () -> {
                    Pose2d botPose = PoseEstimation.getInstance().getEstimatedPose();
                    List<Translation2d> optimalPoints;
                    if (isRed()) {
                        speakerPose = ScoreStateConstants.SPEAKER_POSE_RED;
                        optimalPoints = ScoreStateConstants.OPTIMAL_POINTS_SHOOT_RED;
                    } else {
                        speakerPose = ScoreStateConstants.SPEAKER_POSE_BLUE;
                        optimalPoints = ScoreStateConstants.OPTIMAL_POINTS_SHOOT_BLUE;
                    }
                    Translation2d optimalTranslation;
                    if (Utils.getDistanceFromPoint(speakerPose, botPose)
                            < ScoreStateConstants.MAX_SHOOTING_DISTANCE.in(Units.Meters)) {
                        optimalTranslation = botPose.getTranslation();
                        return SwerveDrive.getInstance()
                                .turnCommand(
                                        Utils.calcRotationToTranslation(
                                                        optimalTranslation, speakerPose)
                                                .getRotations(),
                                        ScoreStateConstants.TURN_TOLERANCE.in(Units.Rotations));
                    }
                    optimalTranslation = botPose.getTranslation().nearest(optimalPoints);

                    return AutoBuilder.pathfindToPose(
                            new Pose2d(
                                    optimalTranslation,
                                    Utils.calcRotationToTranslation(
                                            optimalTranslation, speakerPose)),
                            Constants.AUTO_CONSTRAINTS);
                },
                Set.of(SwerveDrive.getInstance()));
    }

    @Override
    public Command initializeCommand() {
        return shooter.setVelocity(
                () ->
                        Units.RotationsPerSecond.of(
                                        ShooterConstants.interpolationMap.get(
                                                        Utils.getDistanceFromPoint(
                                                                speakerPose,
                                                                PoseEstimation.getInstance()
                                                                        .getEstimatedPose()))
                                                .value)
                                .mutableCopy());
    }

    @Override
    public Command initializeSubsystem() {
        return null;
    }

    @Override
    public Command score() {
        return null;
    }
}
