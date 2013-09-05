package tk.amberide.ide.gui.editor.map.tool._3d;

import tk.amberide.engine.data.map.Tile;
import tk.amberide.ide.data.res.Tileset;
import tk.amberide.engine.gl.camera.EulerCamera;
import tk.amberide.ide.gui.editor.map.MapContext;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author Tudor
 */
public class Fill3D extends Brush3D {

    public Fill3D(MapContext context, EulerCamera camera) {
        super(context, camera);
    }

    @Override
    public boolean apply(int x, int y, int z) {
        boolean modified = false;
        if (isInBounds(x, y)) {
            Tileset.TileSprite target = spriteAt(x, y, z);
            Stack<Point> stack = new Stack<Point>() {
                Set<Point> visited = new HashSet<Point>();

                @Override
                public Point push(Point t) {
                    return visited.add(t) ? super.push(t) : t;
                }
            };

            stack.push(new Point(x, y));
            while (!stack.empty()) {
                Point p = stack.pop();
                if (spriteAt(p.x, p.y, z) != target) {
                    continue;
                }
                if (super.apply(p.x, p.y, z)) {
                    modified = true;
                }

                if (target == spriteAt(p.x - 1, p.y, z)) {
                    stack.push(new Point(p.x - 1, p.y));
                }
                if (target == spriteAt(p.x + 1, p.y, z)) {
                    stack.push(new Point(p.x + 1, p.y));
                }
                if (target == spriteAt(p.x, p.y - 1, z)) {
                    stack.push(new Point(p.x, p.y - 1));
                }
                if (target == spriteAt(p.x, p.y + 1, z)) {
                    stack.push(new Point(p.x, p.y + 1));
                }
            }
        }
        return modified;
    }

    private Tileset.TileSprite spriteAt(int x, int y, int z) {
        if (isInBounds(x, y)) {
            Tile tile = context.map.getLayer(context.layer).getTile(x, y, z);
            return tile != null ? tile.getSprite() : Tileset.TileSprite.NULL_SPRITE;
        }
        return null;
    }
}
