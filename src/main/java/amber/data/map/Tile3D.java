package amber.data.map;

import amber.data.res.Tileset;

/**
 *
 * @author Tudor
 */
public class Tile3D extends Tile {

    private Direction dir;
    private Angle angle;

    public Tile3D(Tileset.TileSprite sprite, Direction dir, Angle angle) {
        super(sprite);
        this.dir = dir;
        this.angle = angle;
    }

    public Tile3D(Tileset.TileSprite sprite, Direction dir) {
        this(sprite, dir, Angle._180);
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return dir;
    }

    /**
     * @param dir the direction to set
     */
    public void setDirection(Direction dir) {
        this.dir = dir;
    }

    /**
     * @return the angle
     */
    public Angle getAngle() {
        return angle;
    }

    /**
     * @param dir the direction to set
     */
    public void setAngle(Angle angle) {
        this.angle = angle;
    }

    public Tile3D clone() {
        return new Tile3D(sprite, dir, angle);
    }

    public static enum Angle {

        _45(45), _90(90), _180(0);
        private int angle;

        Angle(int angle) {
            this.angle = angle;
        }

        public int intValue() {
            return angle;
        }
    }
}
