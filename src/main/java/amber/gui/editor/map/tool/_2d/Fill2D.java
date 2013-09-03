package amber.gui.editor.map.tool._2d;

import amber.data.map.Layer;
import amber.data.map.Tile;
import amber.data.res.Tileset;
import amber.gui.editor.map.MapContext;
import java.awt.Point;
import java.util.HashSet;
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
        int minX, minY, width, height;
        Tileset.TileSprite[][] toFind;

        public FloodFiller(Fill2D tool, Point start) {
            this.tool = tool;
            this.start = start;
            minX = start.x;
            minY = start.y;
            width = tool.context.tileSelection.length;
            height = tool.context.tileSelection[0].length;
            layer = context.map.getLayer(context.layer);

            toFind = new Tileset.TileSprite[width][height];
            for (int x = 0; x < width; ++x)
                for (int y = 0; y < height; ++y)
                    toFind[x][y] = tool.spriteAt(start.x + x, start.y + y);
        }

        public void buildList() {
            Stack<Point> toVisit = new Stack<Point>();
            toVisit.push(start);
            while (!toVisit.empty()) {
                Point point = toVisit.pop();
                if (!tool.isInBounds(point.x, point.y))
                    continue;
                if (tool.spriteAt(point.x, point.y) != toFind[((start.x - point.x) % width + width) % width][((start.y - point.y) % height + height) % height])
                    continue;
                if (!toFill.add(point))
                    continue;
                if (point.x < minX)
                    minX = point.x;
                if (point.y < minY)
                    minY = point.y;
                toVisit.push(new Point(point.x - 1, point.y));
                toVisit.push(new Point(point.x, point.y + 1));
                toVisit.push(new Point(point.x + 1, point.y));
                toVisit.push(new Point(point.x, point.y - 1));
            }
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
