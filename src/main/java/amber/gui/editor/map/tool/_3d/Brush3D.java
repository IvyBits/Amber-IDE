package amber.gui.editor.map.tool._3d;

import amber.data.map.Direction;
import static amber.data.map.Direction.EAST;
import static amber.data.map.Direction.NORTH;
import static amber.data.map.Direction.SOUTH;
import static amber.data.map.Direction.WEST;
import amber.data.map.Layer;
import amber.data.map.Layer3D;
import amber.data.map.Tile;
import amber.data.map.Tile3D;
import static amber.data.map.Tile3D.Angle._180;
import static amber.data.map.Tile3D.Angle._45;
import static amber.data.map.Tile3D.Angle._90;
import amber.data.map.TileModel;
import amber.data.math.Angles;
import amber.data.res.Tileset;
import amber.gl.camera.EulerCamera;
import amber.gl.tess.ITesselator;
import amber.gl.tess.ImmediateTesselator;
import amber.gui.editor.map.MapContext;
import java.awt.Component;
import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_POLYGON_BIT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Tudor
 */
public class Brush3D extends AbstractTool3D {

    protected EulerCamera camera;
    protected Tile3D.Angle angle = Tile3D.Angle._180;
    protected ITesselator tess = new ImmediateTesselator();

    public Brush3D(MapContext context, EulerCamera camera) {
        super(context);
        this.camera = camera;
    }

    public boolean apply(int x, int y, int z) {
        boolean modified = false;
        switch (context.drawType) {
            case MapContext.TYPE_TILE:
                if (context.tileSelection != null) {
                    Tileset.TileSprite[][] sel = context.tileSelection.clone();
                    int w = sel.length;
                    int h = sel[0].length;
                    for (int sx = 0; sx != w; sx++) {
                        for (int sy = 0; sy != h; sy++) {
                            boolean tiled = setAngledTile(x, y, z, sx, sy, h, w, camera.getFacingDirection(), angle, sel);
                            if (!modified) {
                                modified = tiled;
                            }
                        }
                    }
                }
                break;
            case MapContext.EXT_TYPE_MODEL:
                if (context.EXT_modelSelection != null) {
                    Layer l = context.map.getLayer(context.layer);
                    if (l instanceof Layer3D) {
                        Layer3D l3d = (Layer3D) l;
                        TileModel p = l3d.getModel(x, y, z);
                        if (isInBounds(x, y) && p == null || p.getModel() != context.EXT_modelSelection) {
                            l3d.setModel(x, y, z, new TileModel(context.EXT_modelSelection));
                            modified = true;
                        }
                    }
                }
                break;
        }

        return modified;
    }

    protected final boolean setAngledTile(int x, int y, int z, int sx, int sy, int h, int w, Direction dir, Tile3D.Angle angle, Tileset.TileSprite[][] sel) {
        // It works, that's all you need to know.
        switch (angle) {
            case _180:
                switch (dir) {
                    case NORTH:
                        return setTile(x + sy, y + sx, z, sel[sx][h - sy - 1], dir, angle);
                    case EAST:
                        return setTile(sx + x - w + 1, sy + y, z, sel[w - sx - 1][h - sy - 1], dir, angle);
                    case SOUTH:
                        return setTile(x + sy - h + 1, y + sx - w + 1, z, sel[w - sx - 1][sy], dir, angle);
                    case WEST:
                        return setTile(sx + x, sy + y - h + 1, z, sel[sx][sy], dir, angle);
                }
            case _90:
                switch (dir) {
                    case NORTH:
                        return setTile(x, y + sx, z + sy, sel[sx][h - sy - 1], dir, angle);
                    case EAST:
                        return setTile(sx + x - w + 1, y, z + sy, sel[w - sx - 1][h - sy - 1], dir, angle);
                    case SOUTH:
                        return setTile(x, y + sx - w + 1, z + sy, sel[w - sx - 1][h - sy - 1], dir, angle);
                    case WEST:
                        return setTile(sx + x, y, z + sy, sel[sx][h - sy - 1], dir, angle);
                }
            case _45:
                switch (dir) {
                    case NORTH:
                        return setTile(x + sy, y + sx, z + sy, sel[sx][h - sy - 1], dir, angle);
                    case EAST:
                        return setTile(sx + x - w + 1, sy + y, z + sy, sel[w - sx - 1][h - sy - 1], dir, angle);
                    case SOUTH:
                        return setTile(x - sy, y + sx - w + 1, z + sy, sel[w - sx - 1][h - sy - 1], dir, angle);
                    case WEST:
                        return setTile(sx + x, y - sy, z + sy, sel[sx][h - sy - 1], dir, angle);
                }
        }
        return false;
    }

    protected boolean setTile(int x, int y, int z, Tileset.TileSprite tile, Direction dir, Tile3D.Angle angle) {
        Layer lay = context.map.getLayer(context.layer);
        boolean modified = false;
        if (isInBounds(x, y)) {
            Tile r = lay.getTile(x, y, z);
            if (tile != null) {
                modified = r == null || !tile.equals(r.getSprite());
                lay.setTile(x, y, z, new Tile3D(tile, dir, angle));
            } else {
                modified = r != null;
                lay.setTile(x, y, z, null);
            }
        }
        return modified;
    }

    public void draw(int x, int y, int z) {
        if (context.drawType == MapContext.EXT_TYPE_MODEL && context.EXT_modelSelection != null) {
            glPushAttrib(GL_CURRENT_BIT | GL_POLYGON_BIT);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            tess.drawModel3D(new TileModel(context.EXT_modelSelection), x, z, y);
            glPopAttrib();
        } else if (context.tileSelection != null) {
            glPushMatrix();
            float[] tr = new float[]{x, y, z, 0};

            switch (camera.getFacingDirection()) {
                case SOUTH:
                    tr[0]++;
                    tr[2]++;
                    tr[3] = 180;
                    break;
                case WEST:
                    tr[2]++;
                    tr[3] = 90;
                    break;
                case EAST:
                    tr[0]++;
                    tr[3] = 270;
                    break;
            }
            glTranslatef(tr[0], tr[1], tr[2]);
            glRotatef(tr[3], 0, 1, 0);
            Vector2f ix = Angles.circleIntercept(angle.intValue(), 0, 0, context.tileSelection[0].length);
            glBegin(GL_LINE_LOOP);
            {
                int w = context.tileSelection.length;
                glVertex3f(0, 0, 0);
                glVertex3f(ix.x, ix.y, 0);
                glVertex3f(ix.x, ix.y, w);
                glVertex3f(0, 0, w);
            }
            glEnd();
            glPopMatrix();
        }
    }

    public Component getContextComponent() {
        return null;
    }
}
