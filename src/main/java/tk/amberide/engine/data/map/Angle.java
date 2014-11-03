package tk.amberide.engine.data.map;

/**
 *
 * @author Tudor
 */
public enum Angle {

    SLANTED(45), VERTICAL(90), HORIZONTAL(0);
    private int angle;

    Angle(int angle) {
        this.angle = angle;
    }

    public int intValue() {
        return angle;
    }
}