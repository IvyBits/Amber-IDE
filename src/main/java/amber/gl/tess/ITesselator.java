package amber.gl.tess;

import amber.data.map.Tile;
import amber.data.map.Tile3D;

/**
 *
 * @author Tudor
 */
public interface ITesselator {

    void drawTile3D(Tile3D tile, int x, int y, int z);

    void drawTile2D(Tile tile, int x, int y);
}
