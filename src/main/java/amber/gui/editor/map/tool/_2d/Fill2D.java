package amber.gui.editor.map.tool._2d;

import amber.data.map.Layer;
import amber.data.map.Tile;
import amber.data.res.Tileset;
import amber.gui.editor.map.MapContext;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Tudor
 */
public class Fill2D extends Brush2D {

    public Fill2D(MapContext context) {
        super(context);
    }

    protected class FloodFiller {
        private final Fill2D tool;
        private final Point start;
        private final Layer layer;
        private HashSet<Point> toFill = new HashSet<Point>();
        private Tileset.TileSprite toFind;
        int minX, minY, width, height;

        public FloodFiller(Fill2D tool, Point start) {
            this.tool = tool;
            this.start = start;
            toFind = tool.spriteAt(start.x, start.y);
            minX = start.x;
            minY = start.y;
            width = tool.context.tileSelection.length;
            height = tool.context.tileSelection[0].length;
            layer = context.map.getLayer(context.layer);
        }

        public void buildList() {
            buildList(start.x, start.y);
        }

        protected void buildList(int x, int y) {
            if (!tool.isInBounds(x, y))
                return;
            if (tool.spriteAt(x, y) != toFind)
                return;
            if (!toFill.add(new Point(x, y)))
                return;
            if (x < minX)
                minX = x;
            if (y < minY)
                minY = y;
            buildList(x - 1, y);
            buildList(x, y + 1);
            buildList(x + 1, y);
            buildList(x, y - 1);
        }

        public boolean fill() {
            boolean modified = false;
            for (Point point : toFill) {
                int x = point.x, y = point.y;
                Tileset.TileSprite sprite = context.tileSelection[(x - minX) % width][ (y - minY) % height];
                if (!modified) {
                    Tile old = layer.getTile(x, y, 0);
                    modified = old == null || !sprite.equals(old.getSprite());
                }
                layer.setTile(x, y, 0, new Tile(sprite));
            }
            return modified;
        }
    }

    public boolean apply(int x, int y) {
        FloodFiller filler = new FloodFiller(this, new Point(x, y));
        filler.buildList();
        return filler.fill();
    }

    protected Tileset.TileSprite spriteAt(int x, int y) {
        if (isInBounds(x, y)) {
            Tile tile = context.map.getLayer(context.layer).getTile(x, y, 0);
            return tile != null ? tile.getSprite() : Tileset.TileSprite.NULL_SPRITE;
        }
        return null;
    }
}
