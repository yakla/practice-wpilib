package frc.robot.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.subsystems.swerve.SwerveDrive;
import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalDouble;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;

public class PhotonVisionIOReal implements VisionIO {

    private final PhotonCamera camera;
    private final PhotonPoseEstimator estimator;
    private final Transform3d robotToCamera;
    private final boolean calculateScoreParams;
    private boolean isNoteDetector;
    private VisionResult result =
            new VisionResult(
                    new EstimatedRobotPose(
                            new Pose3d(),
                            0,
                            new ArrayList<>(),
                            PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR),
                    false);
    private VisionResult lastResult =
            new VisionResult(
                    new EstimatedRobotPose(
                            new Pose3d(),
                            0,
                            new ArrayList<>(),
                            PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR),
                    false);
    private Optional<ScoreParameters> scoreParameters = Optional.empty();
    private OptionalDouble yawToNote = OptionalDouble.empty();
    private final String name;

    public PhotonVisionIOReal(
            PhotonCamera camera,
            String name,
            Transform3d robotToCamera,
            AprilTagFieldLayout field,
            boolean calculateScoreParams,
            boolean isNoteDetector) {
        this.camera = camera;
        this.name = name;
        this.robotToCamera = robotToCamera;
        this.calculateScoreParams = calculateScoreParams;
        this.isNoteDetector = isNoteDetector;
        camera.setPipelineIndex(0);
        estimator =
                new PhotonPoseEstimator(
                        field,
                        PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
                        camera,
                        robotToCamera);
    }

    @Override
    public void setPipeLine(int pipeLineIndex) {
        camera.setPipelineIndex(pipeLineIndex);
    }

    @Override
    public OptionalDouble getYawToNote() {
        return yawToNote;
    }

    @Override
    public Optional<ScoreParameters> getScoreParameters() {
        return scoreParameters;
    }

    @Override
    public void updateInputs(VisionInputs inputs) {
        inputs.isConnected = camera.isConnected();

        var latestResult = camera.getLatestResult();

        if (isNoteDetector && latestResult.hasTargets()) {
            inputs.yawNote = latestResult.getBestTarget().getYaw();
            yawToNote = OptionalDouble.of(inputs.yawNote);
        } else {
            yawToNote = OptionalDouble.empty();
        }

        var estimatedPose = estimator.update(latestResult);
        if (estimatedPose.isPresent()) {
            inputs.poseFieldOriented = estimatedPose.get().estimatedPose;

            result = new VisionResult(estimatedPose.get(), true);
            double distanceTraveled =
                    result.getEstimatedRobotPose()
                            .estimatedPose
                            .minus(lastResult.getEstimatedRobotPose().estimatedPose)
                            .getTranslation()
                            .getNorm();
            if ((DriverStation.isEnabled() && distanceTraveled > 0.2)
                    || (result.getEstimatedRobotPose().estimatedPose.getZ() > 0.1)
                    || (VisionConstants.outOfBounds(result.getEstimatedRobotPose().estimatedPose))
                    || result.getEstimatedRobotPose().targetsUsed.stream()
                            .anyMatch(
                                    (target) ->
                                            target.getBestCameraToTarget()
                                                            .getTranslation()
                                                            .getNorm()
                                                    > 5.0)) {
                result.setUseForEstimation(false);
            }
        } else {
            result.setUseForEstimation(false);
        }

        if (calculateScoreParams && latestResult.hasTargets()) {
            var toSpeaker =
                    inputs.poseFieldOriented
                            .getTranslation()
                            .toTranslation2d()
                            .minus(VisionConstants.getSpeakerPose());
            inputs.distanceToSpeaker = toSpeaker.getNorm();
            var centerTag = latestResult.getTargets();
            centerTag.removeIf(
                    (target) -> target.getFiducialId() != VisionConstants.getSpeakerTag1());
            Optional<Rotation2d> yaw;
            if (!centerTag.isEmpty()) {
                inputs.yawToSpeaker = Rotation2d.fromDegrees(-centerTag.get(0).getYaw());
                yaw = Optional.of(inputs.yawToSpeaker);
            } else {
                yaw = Optional.empty();
            }
            scoreParameters =
                    Optional.of(
                            new ScoreParameters(
                                    toSpeaker,
                                    yaw,
                                    new Rotation2d(toSpeaker.getX(), toSpeaker.getY())
                                            .minus(SwerveDrive.getInstance().getOdometryYaw())));
        } else {
            scoreParameters = Optional.empty();
        }

        lastResult = result;
    }

    @Override
    public VisionResult getLatestResult() {
        return result;
    }

    @Override
    public Transform3d getCameraToRobot() {
        return robotToCamera;
    }

    @Override
    public String getName() {
        return name;
    }
}
