package tk.amberide.ide.gui.editor.map.tool;

import tk.amberide.engine.data.map.Layer;
import tk.amberide.engine.data.map.Tile;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.gl.camera.EulerCamera;
import tk.amberide.ide.gui.editor.map.MapContext;
import java.awt.Point;
import java.util.HashSet;
import java.util.Stack;

/**
 *
 * @author Tudor
 */
public class FillTool extends BrushTool {

    public FillTool(MapContext context, EulerCamera camera) {
        super(context, camera);
    }

    protected class FloodFiller {
        private final FillTool tool;
        private final Point start;
        private final Layer layer;
        private HashSet<Point> toFill = new HashSet<Point>();
        int minX, minY, width, height, z;
        Tileset.TileSprite[][] toFind;

        public FloodFiller(FillTool tool, Point start, int z) {
            this.tool = tool;
            this.start = start;
            this.z = z;
            minX = start.x;
            minY = start.y;
            width = tool.context.tileSelection.length;
            height = tool.context.tileSelection[0].length;
            layer = context.map.getLayer(context.layer);

            toFind = new Tileset.TileSprite[width][height];
            for (int x = 0; x < width; ++x)
                for (int y = 0; y < height; ++y)
                    toFind[x][y] = tool.spriteAt(start.x + x, start.y + y, z);
        }

        public void buildList() {
            Stack<Point> toVisit = new Stack<Point>();
            toVisit.push(start);
            while (!toVisit.empty()) {
                Point point = toVisit.pop();
                if (!tool.isInBounds(point.x, point.y))
                    continue;
                if (tool.spriteAt(point.x, point.y, z) != toFind[((start.x - point.x) % width + width) % width][((start.y - point.y) % height + height) % height])
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
                layer.setTile(x, y, z, new Tile(sprite, tool.camera.getFacingDirection()));
            }
            return modified;
        }
    }

    public boolean apply(int x, int y, int z) {
        FloodFiller filler = new FloodFiller(this, new Point(x, y), z);
        filler.buildList();
        return filler.fill();
    }

    private Tileset.TileSprite spriteAt(int x, int y, int z) {
        if (isInBounds(x, y)) {
            Tile tile = context.map.getLayer(context.layer).getTile(x, y, z);
            return tile != null ? tile.getSprite() : Tileset.TileSprite.NULL_SPRITE;
        }
        return null;
    }
}
