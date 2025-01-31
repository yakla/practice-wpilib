package frc.robot.subsystems.swerve;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.lib.Utils;
import frc.robot.lib.units.Units;

public class ModuleIOSparkMax implements ModuleIO {

    private final CANSparkMax driveMotor;
    private final SparkPIDController drivePIDController;
    private final RelativeEncoder driveEncoder;
    private final CANSparkMax angleMotor;
    private final SparkPIDController anglePIDController;
    private final RelativeEncoder angleEncoder;

    private final DutyCycleEncoder encoder;

    private SimpleMotorFeedforward feedforward;
    private final SwerveModuleInputsAutoLogged inputs;

    public ModuleIOSparkMax(
            int driveMotorID,
            int angleMotorID,
            int encoderID,
            boolean driveInverted,
            boolean angleInverted,
            SwerveModuleInputsAutoLogged inputs) {
        this.inputs = inputs;

        this.driveMotor = new CANSparkMax(driveMotorID, CANSparkBase.MotorType.kBrushless);
        this.angleMotor = new CANSparkMax(angleMotorID, CANSparkBase.MotorType.kBrushless);

        this.encoder = new DutyCycleEncoder(encoderID);

        driveMotor.restoreFactoryDefaults();
        drivePIDController = driveMotor.getPIDController();
        driveEncoder = driveMotor.getEncoder();

        driveMotor.enableVoltageCompensation(
                frc.robot.subsystems.swerve.SwerveConstants.VOLT_COMP_SATURATION);
        driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        driveMotor.setSmartCurrentLimit(
                (int) frc.robot.subsystems.swerve.SwerveConstants.NEO_CURRENT_LIMIT);
        driveMotor.setInverted(driveInverted);
        driveEncoder.setPositionConversionFactor(
                frc.robot.subsystems.swerve.SwerveConstants.DRIVE_REDUCTION);
        driveEncoder.setVelocityConversionFactor(
                frc.robot.subsystems.swerve.SwerveConstants.DRIVE_REDUCTION);
        driveMotor.burnFlash();

        angleMotor.restoreFactoryDefaults();
        anglePIDController = angleMotor.getPIDController();
        updatePID();
        angleEncoder = angleMotor.getEncoder();

        angleMotor.enableVoltageCompensation(
                frc.robot.subsystems.swerve.SwerveConstants.VOLT_COMP_SATURATION);
        angleMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        angleMotor.setSmartCurrentLimit(
                (int) frc.robot.subsystems.swerve.SwerveConstants.NEO_550_CURRENT_LIMIT);
        angleMotor.setInverted(angleInverted);
        angleEncoder.setPositionConversionFactor(
                frc.robot.subsystems.swerve.SwerveConstants.ANGLE_REDUCTION);
        angleEncoder.setVelocityConversionFactor(
                frc.robot.subsystems.swerve.SwerveConstants.ANGLE_REDUCTION);
        angleMotor.burnFlash();
    }

    @Override
    public void updateInputs() {
        inputs.absolutePosition = getEncoderAngle();

        inputs.driveMotorPosition = driveEncoder.getPosition();
        inputs.driveMotorVelocity = getVelocity();

        inputs.angle =
                Rotation2d.fromRadians(Utils.normalize(angleEncoder.getPosition() * 2 * Math.PI));

        inputs.moduleDistance =
                inputs.driveMotorPosition
                        * frc.robot.subsystems.swerve.SwerveConstants.WHEEL_DIAMETER
                        * Math.PI;

        if (hasPIDChanged(frc.robot.subsystems.swerve.SwerveConstants.PID_VALUES)) updatePID();
    }

    @Override
    public SwerveModuleInputsAutoLogged getInputs() {
        return inputs;
    }

    @Override
    public void updatePID() {
        feedforward =
                new SimpleMotorFeedforward(
                        frc.robot.subsystems.swerve.SwerveConstants.DRIVE_KS.get(),
                        frc.robot.subsystems.swerve.SwerveConstants.DRIVE_KV.get(),
                        frc.robot.subsystems.swerve.SwerveConstants.DRIVE_KA.get());
        anglePIDController.setP(frc.robot.subsystems.swerve.SwerveConstants.ANGLE_KP.get());
        anglePIDController.setI(frc.robot.subsystems.swerve.SwerveConstants.ANGLE_KI.get());
        anglePIDController.setD(frc.robot.subsystems.swerve.SwerveConstants.ANGLE_KD.get());
        anglePIDController.setFF(frc.robot.subsystems.swerve.SwerveConstants.ANGLE_KS.get());
    }

    @Override
    public Rotation2d getAngle() {
        return inputs.angle;
    }

    @Override
    public void setAngle(Rotation2d angle) {
        inputs.angleSetpoint = Utils.normalize(angle);
        Rotation2d error = angle.minus(inputs.angle);
        anglePIDController.setReference(
                inputs.angle.getRotations() + error.getRotations(),
                CANSparkMax.ControlType.kPosition);
    }

    @Override
    public double getVelocity() {
        return Units.rpmToRadsPerSec(driveEncoder.getVelocity())
                * (SwerveConstants.WHEEL_DIAMETER / 2);
    }

    @Override
    public void setVelocity(double velocity) {
        var angleError = inputs.angleSetpoint.minus(inputs.angle);
        velocity *= angleError.getCos();
        inputs.driveMotorVelocitySetpoint = velocity;
        drivePIDController.setReference(
                feedforward.calculate(velocity), CANSparkMax.ControlType.kVoltage);
    }

    @Override
    public SwerveModuleState getModuleState() {
        return new SwerveModuleState(getVelocity(), inputs.angle);
    }

    @Override
    public SwerveModulePosition getModulePosition() {
        return new SwerveModulePosition(inputs.moduleDistance, getAngle());
    }

    @Override
    public void stop() {
        driveMotor.stopMotor();
        angleMotor.stopMotor();
    }

    @Override
    public Command checkModule() {
        return Commands.run(
                () -> {
                    driveMotor.set(0.8);
                    angleMotor.set(0.2);
                });
    }

    @Override
    public void updateOffset(Rotation2d offset) {
        angleEncoder.setPosition(getEncoderAngle() - offset.getRotations());
    }

    private double getEncoderAngle() {
        return 1.0 - encoder.getAbsolutePosition();
    }
}
