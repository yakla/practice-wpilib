package frc.robot.subsystems.hood;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.*;
import org.littletonrobotics.junction.AutoLog;

public interface HoodIO {
    HoodInputsAutoLogged inputs = new HoodInputsAutoLogged();

    default void updateInternalEncoder() {}

    default void setAngle(MutableMeasure<Angle> angle) {}

    default void setAngle(MutableMeasure<Angle> angle, double torqueChassisCompensation) {}

    /** Update the inputs of the hood */
    default void updateInputs() {}

    @AutoLog
    class HoodInputs {
        public MutableMeasure<Angle> internalAngle = MutableMeasure.zero(Rotations);
        public MutableMeasure<Angle> angleSetpoint = MutableMeasure.zero(Rotations);
        public MutableMeasure<Voltage> voltage = MutableMeasure.zero(Volts);
        public MutableMeasure<Angle> absoluteEncoderAngle = MutableMeasure.zero(Rotations);
        public MutableMeasure<Angle> absoluteEncoderAngleNoOffset = MutableMeasure.zero(Rotations);
    }
}
