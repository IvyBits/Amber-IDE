package amber.gui.editor.map.tool._2d;

import amber.data.map.Layer;
import amber.data.map.Tile;
import amber.gui.editor.map.MapContext;
import java.awt.Component;

/**
 *
 * @author Tudor
 */
public class Brush2D extends AbstractTool2D {

    public Brush2D(MapContext context) {
        super(context);
    }

    public boolean apply(int x, int y) {
        boolean modified = false;
        if (context.tileSelection != null) {
            Layer lay = context.map.getLayer(context.layer);
            for (int cx = 0; cx != context.tileSelection.length; cx++) {
                for (int cy = 0; cy != context.tileSelection[0].length; cy++) {
                    int mapX = cx + x, mapY = cy + y;
                    if (isInBounds(mapX, mapY)) {
                        // We need to flip the array horizontally, so inverse the y
                        Tile t = new Tile(context.tileSelection[cx][context.tileSelection[0].length - cy - 1]);
                        Tile r = lay.getTile(mapX, mapY, 0);
                        modified = r == null || !t.getSprite().equals(r.getSprite());
                        lay.setTile(mapX, mapY, 0, t);
                    }
                }
            }
        }
        return modified;
    }

    public Component getContextComponent() {
        return null;
    }
}
