package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.robot.scoreStates.ScoreState;
import frc.robot.scoreStates.ScoreStateConstants;

public class VisionConstants {

    public static final double FIELD_LENGTH = 16.54;
    public static final double FIELD_WIDTH = 8.23;

    public static final int SPEAKER1_BLUE = 7;
    public static final int SPEAKER2_BLUE = 8; // TODO: put real num

    public static final int SPEAKER1_RED = 4;
    public static final int SPEAKER2_RED = 3;

    public static final double DISTANCE_BETWEEN_SPEAKER_TAGS = 0.57;
    public static final double SPEAKER_TAGS_HEIGHT = 1.45;

    public static boolean outOfBounds(Pose3d estimatedPose) {
        return estimatedPose.getX() < 0
                || estimatedPose.getX() > FIELD_LENGTH
                || estimatedPose.getY() < 0
                || estimatedPose.getY() > FIELD_WIDTH;
    }

    public static int getSpeakerTag1() {
        if (ScoreState.isRed()) {
            return SPEAKER1_RED;
        }
        return SPEAKER1_BLUE;
    }

    public static int getSpeakerTag2() {
        if (ScoreState.isRed()) {
            return SPEAKER2_RED;
        }
        return SPEAKER2_BLUE;
    }

    public static Translation2d getSpeakerPose() {
        if (ScoreState.isRed()) {
            return ScoreStateConstants.SPEAKER_POSE_RED;
        }
        return ScoreStateConstants.SPEAKER_POSE_BLUE;
    }
}
