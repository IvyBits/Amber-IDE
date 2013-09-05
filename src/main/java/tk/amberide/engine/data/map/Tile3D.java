package tk.amberide.engine.data.map;

import tk.amberide.ide.data.res.Tileset;

/**
 *
 * @author Tudor
 */
public class Tile3D extends Tile {

    protected Direction dir;
    protected Angle angle;
    protected TileType type;

    public Tile3D(Tileset.TileSprite sprite, Direction dir, Angle angle, TileType type) {
        super(sprite);
        this.dir = dir;
        this.angle = angle;
        this.type = type;
    }

    public Tile3D(Tileset.TileSprite sprite, Direction dir, TileType type) {
        this(sprite, dir, Angle._180, type);
    }

    public Tile3D(Tileset.TileSprite sprite, Direction dir, Angle angle) {
        this(sprite, dir, angle, TileType.TILE_NORMAL);
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

    public TileType getType() {
        return type;
    }

    @Override
    public Tile3D clone() {
        return new Tile3D(sprite, dir, angle, type);
    }
}
