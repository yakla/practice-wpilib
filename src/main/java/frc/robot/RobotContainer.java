package frc.robot;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.conveyor.Conveyor;
import frc.robot.subsystems.conveyor.ConveyorConstants;
import frc.robot.subsystems.conveyor.ConveyorIO;
import frc.robot.subsystems.conveyor.ConveyorIOSim;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIO;
import frc.robot.subsystems.elevator.ElevatorIOReal;
import frc.robot.subsystems.elevator.ElevatorIOSim;
import frc.robot.subsystems.intake.*;
import frc.robot.swerve.SwerveDrive;

public class RobotContainer {

    private static RobotContainer INSTANCE = null;
    private final Elevator elevator;
    private final Conveyor conveyor;
    private final SwerveDrive swerveDrive;
    private final Intake intake;
    private final CommandXboxController xboxController = new CommandXboxController(0);
    private final CommandGenericHID keyBoardControl = new CommandGenericHID(0);

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    private RobotContainer() {
        ElevatorIO elevatorIO;
        ConveyorIO conveyorIO;
        IntakeIO intakeIO;
        IntakeConstants.initConstants();
        ConveyorConstants.initialize(Constants.CURRENT_MODE);
        switch (Constants.CURRENT_MODE) {
            case REAL:
                elevatorIO = new ElevatorIOReal();
                conveyorIO = new ConveyorIOSim();
                intakeIO = new IntakeIOReal();
                break;
            case SIM:
            case REPLAY:
            default:
                conveyorIO = new ConveyorIOSim();
                elevatorIO = new ElevatorIOSim();
                intakeIO = new IntakeIOSim();
                break;
        }
        Elevator.initialize(elevatorIO);
        Conveyor.initialize(conveyorIO);
        Intake.initialize(intakeIO);
        Constants.initSwerve();
        Constants.initVision();

        swerveDrive = SwerveDrive.getInstance();
        elevator = Elevator.getInstance();
        conveyor = Conveyor.getInstance();
        intake = Intake.getInstance();
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
        xboxController.a().onTrue(intake.setAngle(Units.Degrees.of(90).mutableCopy()));
        xboxController.b().onTrue(intake.setAngle(Units.Degrees.of(180).mutableCopy()));
        xboxController.x().onTrue(intake.setAngle(Units.Degrees.of(270).mutableCopy()));
        xboxController.y().onTrue(intake.setAngle(Units.Degrees.of(360).mutableCopy()));
        xboxController.rightBumper().onTrue(intake.setRollerSpeed(Units.RotationsPerSecond.of(50).mutableCopy()));
        xboxController.leftBumper().onTrue(intake.setCenterRollerSpeed(0.5));
        //        xboxController.a().onTrue(elevator.setHeight(2));
        //  keyBoardControl
        //          .button(1)
        //          .onTrue(conveyor.setVelocity(() -> Units.RotationsPerSecond.of(50).mutableCopy()));
        //  keyBoardControl
        //          .button(2)
        //          .onTrue(conveyor.setVelocity(() -> Units.RotationsPerSecond.of(0).mutableCopy()));
        //
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
