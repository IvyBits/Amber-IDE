package tk.amberide.engine.data.map;

import tk.amberide.ide.data.res.Tileset;
import tk.amberide.ide.data.res.Tileset.TileSprite;

/**
 *
 * @author Tudor
 */
public class Tile implements Cloneable {

    public TileSprite getSprite() {
        return sprite;
    }

    public void setSprite(TileSprite sprite) {
        this.sprite = sprite;
    }

    protected TileSprite sprite;
    protected Direction dir;
    protected Angle angle;
    protected TileType type;

    public Tile(Tileset.TileSprite sprite, Direction dir, Angle angle, TileType type) {
        this.dir = dir;
        this.angle = angle;
        this.type = type;
        this.sprite = sprite;
    }

    public Tile(Tileset.TileSprite sprite, Direction dir, TileType type) {
        this(sprite, dir, Angle.HORIZONTAL, type);
    }

    public Tile(Tileset.TileSprite sprite, Direction dir, Angle angle) {
        this(sprite, dir, angle, TileType.TILE_NORMAL);
    }

    public Tile(Tileset.TileSprite sprite, Direction dir) {
        this(sprite, dir, Angle.HORIZONTAL);
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
    public Tile clone() {
        return new Tile(sprite, dir, angle, type);
    }
}
