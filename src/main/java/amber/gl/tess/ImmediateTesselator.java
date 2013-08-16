package amber.gl.tess;

import amber.data.math.Angles;
import amber.data.map.Direction;
import static amber.data.map.Direction.*;
import amber.data.map.Tile;
import amber.data.map.Tile3D;
import amber.data.map.Tile3D.Angle;
import amber.data.res.Tileset;
import amber.gl.GLColor;
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
        // This coordinate offsetting is a horrible, hacky way to solve
        // the issue of texture borders bleeding.
        float tx = start.x + .5f;
        float ty = start.y + .5f;
        float th = size.height - 1;
        float tw = size.width - 1;

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

        glPushAttrib(GL_CURRENT_BIT);
        GLColor.GREEN.bind();
        glBegin(GL_LINES);
        {
            glVertex3f(ix.x, 0, 0);
            glVertex3f(ix.x, 3, 0);
        }
        glEnd();
        glPopAttrib();
        glPopMatrix();
    }

    public void drawTile2D(Tile tile, int x, int y) {
        // TODO
    }

    public Vector2f[] tileVertices2D(int x, int y) {
        return new Vector2f[0];
    }

    public Vector2f[] tileTexture2D(Tileset.TileSprite sprite) {
        return  new Vector2f[0];
    }
}
