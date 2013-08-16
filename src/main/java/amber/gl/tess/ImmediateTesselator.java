package amber.gl.tess;

import amber.data.math.Angles;
import static amber.data.map.Direction.*;
import amber.data.map.Tile;
import amber.data.map.Tile3D;
import java.awt.Dimension;
import java.awt.Point;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 * Renders tiles in immediate mode.
 *
 * @author Tudor
 */
public class ImmediateTesselator implements ITesselator {

    public void drawTile3D(Tile3D tile, int x, int y, int z) {
        glPushMatrix();
        float[] tr = new float[]{x, z, y, 0};

        switch (tile.getDirection()) {
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
        Vector2f ix = Angles.circleIntercept(tile.getAngle().intValue(), 0, 0, 1);

        Point start = tile.getSprite().getStart();
        Dimension size = tile.getSprite().getSize();

        float tx = start.x;
        float ty = start.y;
        float th = size.height;
        float tw = size.width;

        glBegin(GL_QUADS);
        {
            glTexCoord2f(tx, ty + th);
            glVertex3f(0, 0, 0);
            glTexCoord2f(tx, ty);
            glVertex3f(ix.x, ix.y, 0);
            glTexCoord2f(tx + tw, ty);
            glVertex3f(ix.x, ix.y, 1);
            glTexCoord2f(tx + tw, ty + th);
            glVertex3f(0, 0, 1);
        }
        glEnd();
        glPopMatrix();
    }

    public void drawTile2D(Tile tile, int x, int y) {
        Point start = tile.getSprite().getStart();
        Dimension size = tile.getSprite().getSize();

        float tx = start.x;
        float ty = start.y;
        float th = size.height;
        float tw = size.width;

        int dx = x * 32;
        int dy = y * 32;

        glBegin(GL_TRIANGLES);
        {
            //0
            glTexCoord2f(tx, ty + th);
            glVertex2f(dx, dy);
            //1
            glTexCoord2f(tx, ty);
            glVertex2f(dx, dy + 32);
            //2
            glTexCoord2f(tx + tw, ty);
            glVertex2f(dx + 32, dy + 32);

            //3 
            glTexCoord2f(tx + tw, ty + th);
            glVertex2f(dx + 32, dy);
            //2
            glTexCoord2f(tx + tw, ty);
            glVertex2f(dx + 32, dy + 32);
            //0
            glTexCoord2f(tx, ty + th);
            glVertex2f(dx, dy);
        }
        glEnd();
    }
}
