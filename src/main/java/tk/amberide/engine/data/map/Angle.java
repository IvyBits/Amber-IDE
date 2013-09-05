package tk.amberide.engine.data.map;

/**
 *
 * @author Tudor
 */
public enum Angle {

    _45(45), _90(90), _180(0);
    private int angle;

    Angle(int angle) {
        this.angle = angle;
    }

    public int intValue() {
        return angle;
    }
}