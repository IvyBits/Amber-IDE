package tk.amberide.engine.data.map;

import tk.amberide.ide.data.res.Tileset.TileSprite;

/**
 *
 * @author Tudor
 */
public class Tile implements Cloneable {

    protected TileSprite sprite;

    public Tile(TileSprite sprite) {
        this.sprite = sprite;
    }

    /**
     * @return the sprite
     */
    public TileSprite getSprite() {
        return sprite;
    }

    /**
     * @param sprite the sprite to set
     */
    public void setSprite(TileSprite sprite) {
        this.sprite = sprite;
    }
    
    public Tile clone() {
        return new Tile(sprite);
    }
}
