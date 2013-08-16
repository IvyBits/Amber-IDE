package amber.gl.tess;

import amber.Angles;
import amber.data.map.Direction;
import static amber.data.map.Direction.EAST;
import static amber.data.map.Direction.NORTH;
import static amber.data.map.Direction.SOUTH;
import static amber.data.map.Direction.WEST;
import amber.data.map.Tile;
import amber.data.map.Tile3D;
import amber.data.map.Tile3D.Angle;
import amber.data.res.Tileset;
import java.awt.Dimension;
import java.awt.Point;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Tudor
 */
public class DefaultTesselator implements ITesselator {

    public void drawTile3D(Tile3D tile, int x, int y, int z) {
        Vector2f[] texture = tileTexture3D(tile.getSprite(), tile.getDirection());
        Vector2f t0 = texture[0];
        Vector2f t1 = texture[1];
        Vector2f t2 = texture[2];
        Vector2f t3 = texture[3];

        Direction face = tile.getDirection();
        Angle angle = tile.getAngle();
        glPushMatrix();
        float[] tr = new float[]{x, z, y, 0};

        switch (face) {
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
        Vector2f ix = Angles.circleIntercept(angle.intValue(), 0, 0, 1);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(t0.x, t0.y);
            glVertex3f(0, 0, 0);
            glTexCoord2f(t1.x, t1.y);
            glVertex3f(ix.x, ix.y, 0);
            glTexCoord2f(t2.x, t2.y);
            glVertex3f(ix.x, ix.y, 1);
            glTexCoord2f(t3.x, t3.y);
            glVertex3f(0, 0, 1);
        }
        glEnd();
        glPopMatrix();
    }

    public void drawTile2D(Tile tile, int x, int y) {
        // TODO
    }

    public Vector2f[] tileVertices2D(int x, int y) {
        return new Vector2f[0];
    }

    public Vector2f[] tileTexture3D(Tileset.TileSprite sprite, Direction dir) {
        Point start = sprite.getStart();
        Dimension size = sprite.getSize();
        // This coordinate offsetting is a horrible, hacky way to solve
        // the issue of texture borders bleeding.
        float tx = start.x + .5f;
        float ty = start.y + .5f;
        float th = size.height - 1;
        float tw = size.width - 1;
        Vector2f t0 = new Vector2f();
        Vector2f t1 = new Vector2f();
        Vector2f t2 = new Vector2f();
        Vector2f t3 = new Vector2f();
        switch (dir) {
            case NORTH:
                t0.set(tx, ty + th);
                t1.set(tx + tw, ty + th);
                t2.set(tx + tw, ty);
                t3.set(tx, ty);
                break;
            case EAST:
                t0.set(tx + tw, ty + th);
                t1.set(tx + tw, ty);
                t2.set(tx, ty);
                t3.set(tx, ty + th);
                break;
            case SOUTH:
                t0.set(tx + tw, ty);
                t1.set(tx, ty);
                t2.set(tx, ty + th);
                t3.set(tx + tw, ty + th);
                break;
            case WEST:
                t0.set(tx, ty);
                t1.set(tx, ty + th);
                t2.set(tx + tw, ty + th);
                t3.set(tx + tw, ty);
                break;
        }
        return new Vector2f[]{t0, t1, t2, t3};
    }

    public Vector2f[] tileTexture2D(Tileset.TileSprite sprite) {
        return tileTexture3D(sprite, NORTH);
    }
}
