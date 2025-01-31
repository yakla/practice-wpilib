package frc.robot.subsystems.shooter;

import static edu.wpi.first.units.Units.*;

import edu.wpi.first.units.*;
import org.littletonrobotics.junction.AutoLog;

public interface ShooterIO {
    RollerInputsAutoLogged topRollerInputs = new RollerInputsAutoLogged();
    RollerInputsAutoLogged bottomRollerInputs = new RollerInputsAutoLogged();

    default void setTopVelocity(MutableMeasure<Velocity<Angle>> velocity) {}

    default void setBottomVelocity(MutableMeasure<Velocity<Angle>> velocity) {}

    default void stop() {}

    /** Update the inputs of the Shooter */
    default void updateInputs() {}

    @AutoLog
    class RollerInputs {
        public MutableMeasure<Velocity<Angle>> velocity = MutableMeasure.zero(RotationsPerSecond);
        public MutableMeasure<Velocity<Angle>> velocitySetpoint =
                MutableMeasure.zero(RotationsPerSecond);
        public MutableMeasure<Voltage> voltage = MutableMeasure.zero(Volts);
    }
}
