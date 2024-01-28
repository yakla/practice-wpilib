package frc.robot.subsystems.example;

import edu.wpi.first.units.Units;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.hood.Hood;
import frc.robot.subsystems.hood.HoodIO;
import frc.robot.subsystems.hood.HoodIOSim;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HoodTest implements AutoCloseable {
    private final Hood hood;
    private final double Delta = 0.05;

    public HoodTest() {
        HoodIO hoodIO = new HoodIOSim();
        Hood.initialize(hoodIO);
        hood = Hood.getInstance();
    }

    @Test
    public void testSetAngle() throws InterruptedException {
        hood.setDefaultCommand(hood.setAngle(() -> Units.Radians.of(0.5).mutableCopy()));
        CommandScheduler.getInstance().run();
        Thread.sleep(3000);
        Assertions.assertTrue(hood.atSetpoint());
    }

    @Test
    public void testSetPower() throws InterruptedException {
        hood.setDefaultCommand(hood.setPower(() -> 0.5));
        Thread.sleep(3000);
        Assertions.assertEquals(0.5, hood.getVoltage().in(Units.Volts), Delta);
    }

    @Override
    public void close() throws Exception {}
}
