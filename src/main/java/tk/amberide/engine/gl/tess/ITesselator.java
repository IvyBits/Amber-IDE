package tk.amberide.engine.gl.tess;

import tk.amberide.engine.data.map.Tile;
import tk.amberide.engine.data.map.Tile;
import tk.amberide.engine.data.map.TileModel;

/**
 *
 * @author Tudor
 */
public interface ITesselator {
    void startTileBatch();

    void drawTile(Tile tile, float x, float y, float z);

    void endTileBatch();

    void startModelBatch();

    void drawModel(TileModel model, float x, float y, float z);

    void endModelBatch();

    void invalidate();
}
