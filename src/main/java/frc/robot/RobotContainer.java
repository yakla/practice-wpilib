package frc.robot;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commandGroups.CommandGroups;
import frc.robot.lib.GalacticProxyCommand;
import frc.robot.scoreStates.AmpState;
import frc.robot.scoreStates.ClimbState;
import frc.robot.scoreStates.ScoreState;
import frc.robot.scoreStates.ShootState;
import frc.robot.subsystems.conveyor.Conveyor;
import frc.robot.subsystems.conveyor.ConveyorIO;
import frc.robot.subsystems.conveyor.ConveyorIOReal;
import frc.robot.subsystems.conveyor.ConveyorIOSim;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.gripper.Gripper;
import frc.robot.subsystems.gripper.GripperIO;
import frc.robot.subsystems.gripper.GripperIOReal;
import frc.robot.subsystems.gripper.GripperIOSim;
import frc.robot.subsystems.hood.Hood;
import frc.robot.subsystems.hood.HoodIO;
import frc.robot.subsystems.hood.HoodIOReal;
import frc.robot.subsystems.hood.HoodIOSim;
import frc.robot.subsystems.intake.*;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShooterIO;
import frc.robot.subsystems.shooter.ShooterIOReal;
import frc.robot.subsystems.shooter.ShooterIOSim;
import frc.robot.subsystems.swerve.SwerveDrive;

public class RobotContainer {
    private static RobotContainer INSTANCE = null;
    private final Intake intake;
    private final Conveyor conveyor;
    private final Elevator elevator;
    private final Gripper gripper;
    private final Hood hood;
    private final Shooter shooter;
    private final SwerveDrive swerveDrive;
    private final CommandXboxController xboxController = new CommandXboxController(0);

    private CommandGroups commandGroups;
    private ScoreState currentState;
    private final ShootState shootState;
    private final AmpState ampState;
    private final ClimbState climbState;

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    private RobotContainer() {
        HoodIO hoodIO;
        IntakeIO intakeIO;
        ConveyorIO conveyorIO;
        ShooterIO shooterIO;
        GripperIO gripperIO;
        switch (Constants.CURRENT_MODE) {
            case REAL:
                intakeIO = new IntakeIOReal();
                conveyorIO = new ConveyorIOReal();
                gripperIO = new GripperIOReal();
                hoodIO = new HoodIOReal();
                shooterIO = new ShooterIOReal();
                break;
            case SIM:
            case REPLAY:
            default:
                intakeIO = new IntakeIOSim();
                conveyorIO = new ConveyorIOSim();
                gripperIO = new GripperIOSim();
                hoodIO = new HoodIOSim();
                shooterIO = new ShooterIOSim();
                break;
        }
        Intake.initialize(intakeIO);
        Conveyor.initialize(conveyorIO);
        //        Elevator.initialize(elevatorIO);
        Gripper.initialize(gripperIO, () -> Units.Meters.of(0));
        Hood.initialize(hoodIO);
        Shooter.initialize(shooterIO);
        Constants.initSwerve();
        //        Constants.initVision();

        swerveDrive = SwerveDrive.getInstance();
        intake = Intake.getInstance();
        conveyor = Conveyor.getInstance();
        elevator = Elevator.getInstance();
        hood = Hood.getInstance();
        shooter = Shooter.getInstance();

        Gripper.initialize(gripperIO, elevator::getCarriageHeight);
        gripper = Gripper.getInstance();
        commandGroups = CommandGroups.getInstance();

        currentState = new ShootState();
        shootState = new ShootState();
        ampState = new AmpState();
        climbState = new ClimbState();

        // Configure the button bindings and default commands
        configureDefaultCommands();
        configureButtonBindings();
    }

    public static RobotContainer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RobotContainer();
        }
        return INSTANCE;
    }

    private void configureDefaultCommands() {
        swerveDrive.setDefaultCommand(
                swerveDrive.driveCommand(
                        () -> -xboxController.getLeftY(),
                        () -> -xboxController.getLeftX(),
                        () -> -xboxController.getRightX(),
                        0.15,
                        () -> true));
    }

    private void configureButtonBindings() {
        xboxController
                .rightBumper()
                .whileTrue(
                        Commands.parallel(
                                intake.intake(),
                                hood.setAngle(() -> Units.Degrees.of(90).mutableCopy()),
                                gripper.setRollerPower(0.7),
                                conveyor.feed(),
                                shooter.setVelocity(
                                        () -> Units.RotationsPerSecond.of(40).mutableCopy())))
                .onFalse(
                        Commands.parallel(
                                intake.stop(),
                                hood.setAngle(() -> Units.Degrees.of(114).mutableCopy()),
                                gripper.setRollerPower(
                                        0), // Double commands requiring gripper subsystem
                                conveyor.stop(),
                                shooter.stop()));
        xboxController
                .a()
                .whileTrue(hood.setAngle(() -> Units.Degrees.of(90).mutableCopy()))
                .onFalse(hood.setAngle(() -> Units.Degrees.of(114).mutableCopy()));
        xboxController.x().onTrue(intake.reset(Units.Degrees.zero()));
        xboxController.leftBumper().onTrue(Commands.runOnce(swerveDrive::resetGyro));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return null;
    }
}
