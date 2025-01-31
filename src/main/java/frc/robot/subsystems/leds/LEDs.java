package frc.robot.subsystems.leds;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import lombok.Getter;
import lombok.Setter;

public class LEDs extends SubsystemBase {
    private final AddressableLED ledStrip;
    private final AddressableLEDBuffer ledBuffer;

    /**
     * -- SETTER -- Sets the primary color of the LEDs that will be used in the solid and percentage
     * modes and as the first color in the blink and fade modes.
     *
     * @param primary Color to set the primary.
     */
    @Getter @Setter private Color primary = Color.kBlack;

    /**
     * -- SETTER -- Sets the secondary color of the LEDs that will be used as the second color in
     * the blink and fade modes.
     *
     * @param secondary Color to set the secondary.
     */
    @Getter @Setter private Color secondary = Color.kBlack;

    private Color currentColor = primary;
    private Color fadeColor = primary;

    /**
     * -- SETTER -- Sets the duration for the fade effect.
     *
     * @param duration Duration of the fade effect. [sec]
     */
    @Getter @Setter private double fadeTime = 1;

    private final Timer timer = new Timer();

    private int rainbowFirstPixelHue = 0;

    public LEDs(int port, int length) {
        ledStrip = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(length);

        ledStrip.setLength(length);
        ledStrip.setData(ledBuffer);
        ledStrip.start();

        timer.start();
        timer.reset();
    }

    public void setSolidColor(Color color) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setLED(i, color);
        }
        ledStrip.setData(ledBuffer);
    }

    /**
     * Updates fadeColor to the correct color for a fade effect at the current time by interpolating
     * the two given colors.
     *
     * @param initial Initial color.
     * @param goal Final Color.
     */
    public void updateFade(Color initial, Color goal) {
        Translation3d initialPoint = new Translation3d(initial.red, initial.green, initial.blue);
        Translation3d goalPoint = new Translation3d(goal.red, goal.green, goal.blue);

        double t = timer.get() / fadeTime;

        Translation3d solution = initialPoint.interpolate(goalPoint, t);
        fadeColor = new Color((int) solution.getX(), (int) solution.getY(), (int) solution.getZ());
    }

    public void setRainbow() {
        if (timer.advanceIfElapsed(0.05)) {
            for (var i = 0; i < ledBuffer.getLength(); i++) {

                // Calculate the hue - hue is easier for rainbows because the color
                // shape is a circle so only one value needs to precess

                final var hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;

                // Set the value

                ledBuffer.setHSV(i, hue, 255, 128);
            }
            rainbowFirstPixelHue += 8;
            // Check bounds
            rainbowFirstPixelHue %= 180;
            ledStrip.setData(ledBuffer);
        }
        // Increase by to make the rainbow "move"z
    }
}
