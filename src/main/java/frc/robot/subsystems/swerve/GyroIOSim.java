package frc.robot.subsystems.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.lib.math.differential.Integral;

public class GyroIOSim implements GyroIO {
    private final Integral yaw = new Integral();

    @Override
    public Rotation2d getYaw() {
        return new Rotation2d(yaw.get());
    }

    @Override
    public void resetGyro(Rotation2d angle) {
        yaw.override(angle.getRadians());
    }

    @Override
    public void updateInputs(SwerveDriveInputs inputs) {
        yaw.update(inputs.currentSpeeds.omegaRadiansPerSecond);
        inputs.yaw = Rotation2d.fromRadians(yaw.get());
        inputs.rawYaw = inputs.yaw;
    }
}
