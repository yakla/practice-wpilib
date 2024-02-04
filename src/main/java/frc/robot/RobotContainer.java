package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.conveyor.Conveyor;
import frc.robot.subsystems.conveyor.ConveyorConstants;
import frc.robot.subsystems.conveyor.ConveyorIO;
import frc.robot.subsystems.conveyor.ConveyorIOSim;
import frc.robot.subsystems.elevator.Elevator;
import frc.robot.subsystems.elevator.ElevatorIO;
import frc.robot.subsystems.elevator.ElevatorIOReal;
import frc.robot.subsystems.elevator.ElevatorIOSim;
import frc.robot.swerve.SwerveDrive;

public class RobotContainer {

    private static RobotContainer INSTANCE = null;
    private final Elevator elevator;
    private final Conveyor conveyor;
    private final SwerveDrive swerveDrive;
    private final CommandXboxController xboxController = new CommandXboxController(0);

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    private RobotContainer() {
        ElevatorIO elevatorIO;
        ExampleSubsystemIO exampleSubsystemIO;
        switch (Constants.CURRENT_MODE) {
            case REAL:
                elevatorIO = new ElevatorIOReal();
                exampleSubsystemIO = new ExampleSubsystemIOReal();
                ConveyorIO conveyorIO;
                ConveyorConstants.initialize(Constants.CURRENT_MODE);
                switch (Constants.CURRENT_MODE) {
                    case REAL:
                        elevatorIO = new ElevatorIOReal();
                        conveyorIO = new ConveyorIOSim();
                        break;
                    case SIM:
                    case REPLAY:
                    default:
                        exampleSubsystemIO = new ExampleSubsystemIOSim();
                        elevatorIO = new ElevatorIOSim();
                        break;
                }
                ExampleSubsystem.initialize(exampleSubsystemIO);
                Elevator.initialize(elevatorIO);
                conveyorIO = new ConveyorIOSim();
                elevatorIO = new ElevatorIOSim();
                break;
        }
        Elevator.initialize(elevatorIO);
        Conveyor.initialize(conveyorIO);
        Constants.initSwerve();
        Constants.initVision();

        swerveDrive = SwerveDrive.getInstance();
        elevator = Elevator.getInstance();
        conveyor = Conveyor.getInstance();

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

    private void configureButtonBindings() {}

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return null;
    }
}
