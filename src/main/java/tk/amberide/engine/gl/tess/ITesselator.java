package tk.amberide.engine.gl.tess;

import tk.amberide.engine.data.map.Tile;
import tk.amberide.engine.data.map.Tile3D;
import tk.amberide.engine.data.map.TileModel;

/**
 *
 * @author Tudor
 */
public interface ITesselator {

    /**
     * Prepare tesselator to draw tiles.
     */
    void startTileBatch();

    void drawTile3D(Tile3D tile, float x, float y, float z);

    void drawTile2D(Tile tile, float x, float y);

    void endTileBatch();

    void startModelBatch();

    void drawModel3D(TileModel model, float x, float y, float z);

    void endModelBatch();

    void invalidate();
}
