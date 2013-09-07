package tk.amberide.ide.gui.editor.map.tool._2d;

import tk.amberide.ide.data.res.Tileset;
import tk.amberide.ide.gui.editor.map.MapContext;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Tudor
 */
public abstract class AbstractTool2D implements Tool2D {

    protected MapContext context;

    public AbstractTool2D(MapContext context) {
        setContext(context);
    }

    public void setContext(MapContext context) {
        this.context = context;
    }

    public Dimension getPreviewBorderSize() {
        return context.tileSelection != null ? new Dimension(context.tileSelection.length, context.tileSelection[0].length) : new Dimension(0, 0);
    }

    public void doKey(int keycode) {
    }

    public BufferedImage getPreview() {
        Tileset.TileSprite[][] tiles = context.tileSelection;
        if (tiles != null && tiles.length > 0 && tiles[0].length > 0) {
            Dimension size = getPreviewBorderSize(), tileSize = tiles[0][0].getSize();
            BufferedImage tileset = tiles[0][0].getTileset().getImage();
            Point start = tiles[0][0].getStart(), end = (Point) tiles[tiles.length - 1][tiles[0].length - 1].getStart().clone();
            end.translate(tileSize.width, tileSize.height);

            return tileset.getSubimage(start.x, start.y, end.x - start.x, end.y - start.y);
        } else
            return null;
    }

    public float getPreviewOpacity() {
        return .7f;
    }

    protected boolean isInBounds(int x, int y) {
        return !(x < 0 || x > context.map.getWidth() - 1 || y < 0 || y > context.map.getLength() - 1);
    }
}
