package frc.robot.subsystems.hood;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.units.Angle;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.Timer;
import lib.motors.TalonFXSim;

public class HoodIOSim implements HoodIO {
    private final TalonFXSim motor;

    private final MotionMagicDutyCycle control = new MotionMagicDutyCycle(0);
    private final DutyCycleOut dutyCycleOut = new DutyCycleOut(0);
    private final SimpleMotorFeedforward motorFeedforward;

    public HoodIOSim() {
        motor = new TalonFXSim(1, HoodConstants.GEAR_RATIO, HoodConstants.MOMENT_OF_INERTIA, 1);

        motor.setController(
                new PIDController(
                        HoodConstants.kP.get(), HoodConstants.kI.get(), HoodConstants.kD.get()));

        motorFeedforward =
                new SimpleMotorFeedforward(
                        HoodConstants.kS.get(), HoodConstants.kV.get(), HoodConstants.kA.get());
    }

    @Override
    public void updateInternalEncoder() {}

    @Override
    public void setAngle(MutableMeasure<Angle> angle) {
        inputs.controlMode = Mode.ANGLE;
        inputs.angleSetpoint = angle.mut_replace(angle);
        motor.setControl(
                control.withPosition(angle.in(Units.Radians))
                        .withFeedForward(
                                motorFeedforward.calculate(
                                        angle.in(Units.RotationsPerSecond.getUnit()))));
    }

    @Override
    public void setPower(double power) {
        inputs.controlMode = Mode.POWER;
        inputs.powerSetpoint = power;
        motor.setControl(dutyCycleOut.withOutput(power));
    }

    @Override
    public void updateInputs() {
        motor.update(Timer.getFPGATimestamp());

        inputs.angle.mut_replace(motor.getPosition(), Units.Radians);
        inputs.voltage.mut_replace(motor.getAppliedVoltage(), Units.Volts);
    }
}
