package amber.gui.editor.map.tool._3d;

import static amber.data.map.Direction.EAST;
import static amber.data.map.Direction.SOUTH;
import static amber.data.map.Direction.WEST;
import amber.data.math.Angles;
import amber.data.res.Tileset.TileSprite;
import amber.gl.camera.EulerCamera;
import amber.gui.editor.map.MapContext;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Tudor
 */
public class Eraser3D extends Brush3D {

    private int size = 1;

    public Eraser3D(MapContext context, EulerCamera camera) {
        super(context, camera);
    }

    @Override
    public boolean apply(int x, int y, int z) {
        boolean modified = false;
        for (int sx = 0; sx != size; sx++) {
            for (int sy = 0; sy != size; sy++) {
                boolean tiled = setAngledTile(x, y, (int) z, sx, sy, size, size, camera.getFacingDirection(), angle, new TileSprite[][]{{null}});
                if (!modified) {
                    modified = tiled;
                }
            }
        }
        return modified;
    }

    @Override
    public void draw(int x, int y, int z) {
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
        Vector2f ix = Angles.circleIntercept(angle.intValue(), 0, 0, size);
        glBegin(GL_LINE_LOOP);
        {
            glVertex3f(0, 0, 0);
            glVertex3f(ix.x, ix.y, 0);
            glVertex3f(ix.x, ix.y, size);
            glVertex3f(0, 0, size);
        }
        glEnd();
        glPopMatrix();
    }
}
