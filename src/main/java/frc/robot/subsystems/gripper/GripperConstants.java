package frc.robot.subsystems.gripper;

import edu.wpi.first.units.*;
import frc.robot.Constants;
import lib.webconstants.LoggedTunableNumber;

import static frc.robot.subsystems.intake.IntakeConstants.*;

public class GripperConstants {
    public static final Measure<Angle> INTAKE_ANGLE = null;
    public static final Measure<Angle> OUTTAKE_ANGLE = null;
    public static final double INTAKE_POWER = 0;
    public static final double OUTTAKE_POWER = 0;
    public static final Measure<Dimensionless> THRESHOLD = Units.Percent.of(0.02);
    public static final Measure<Distance> GRIPPER_POSITION_X = Units.Meters.of(0);
    public static final Measure<Distance> GRIPPER_POSITION_Y = Units.Meters.of(0);
    public static final Measure<Distance> GRIPPER_POSITION_z = Units.Meters.of(0.6461);
    public static final double ANGLE_MOTOR_GEAR_RATIO = 1;

    public static final LoggedTunableNumber KP = new LoggedTunableNumber("Gripper/kP");
    public static final LoggedTunableNumber KI = new LoggedTunableNumber("Gripper/kI");
    public static final LoggedTunableNumber KD = new LoggedTunableNumber("Gripper/kD");

    public void initConstants() {
        switch (Constants.CURRENT_MODE) {
            case REAL:
                ANGLE_KP.initDefault(0);
                ANGLE_KI.initDefault(0);
                ANGLE_KD.initDefault(0);
                break;
            case SIM:
            case REPLAY:
            default:
                ANGLE_KP.initDefault(0.2);
                ANGLE_KI.initDefault(0);
                ANGLE_KD.initDefault(0);
                break;
        }
    }
}
