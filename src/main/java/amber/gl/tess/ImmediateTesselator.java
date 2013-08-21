package amber.gl.tess;

import amber.data.math.Angles;
import static amber.data.map.Direction.*;
import amber.data.map.Tile;
import amber.data.map.Tile3D;
import amber.data.map.TileModel;
import amber.data.res.Tileset;
import amber.gl.Texture;
import amber.gl.TextureLoader;
import amber.gl.model.ModelScene;
import amber.gl.model.obj.WavefrontObject;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.WeakHashMap;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;
import static org.lwjgl.opengl.ARBTextureRectangle.GL_TEXTURE_RECTANGLE_ARB;

/**
 * Renders tiles in immediate mode.
 *
 * @author Tudor
 */
public class ImmediateTesselator implements ITesselator {

    protected WeakHashMap<Tileset, Texture> textureCache = new WeakHashMap<Tileset, Texture>();
    protected WeakHashMap<WavefrontObject, ModelScene> modelCache = new WeakHashMap<WavefrontObject, ModelScene>();
    protected int bound;

    public void drawTile3D(Tile3D tile, float x, float y, float z) {
        Texture txt = getTexture(tile);
        if (txt.getID() != bound) {
            glBindTexture(txt.getTarget(), bound = txt.getID());
        }
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

    public void drawTile2D(Tile tile, float x, float y) {
        Texture txt = getTexture(tile);
        if (txt.getID() != bound) {
            glBindTexture(txt.getTarget(), bound = txt.getID());
        }
        Point start = tile.getSprite().getStart();
        Dimension size = tile.getSprite().getSize();

        float tx = start.x;
        float ty = start.y;
        float th = size.height;
        float tw = size.width;

        float dx = x * 32;
        float dy = y * 32;

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

    public void startTileBatch() {
        glPushMatrix();
        glPushAttrib(GL_CURRENT_BIT);
        glEnable(GL_TEXTURE_RECTANGLE_ARB);
    }

    public void endTileBatch() {
        bound = -1;
        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glBindTexture(GL_TEXTURE_2D, 0);
        glPopAttrib();
        glPopMatrix();
    }

    public void startModelBatch() {
        glPushMatrix();
    }

    public void drawModel3D(TileModel model, float x, float y, float z) {
        ModelScene scene = getModel(model);
        if (scene != null) {
            glPushMatrix();
            glPushAttrib(GL_CURRENT_BIT | GL_TRANSFORM_BIT);
            glTranslatef(x, z, y);
            scene.draw();
            glTranslatef(-x, -z, -y);
            glPopAttrib();
            glPopMatrix();
            glBindTexture(GL_TEXTURE_2D, 0);
        }
    }

    public void endModelBatch() {
        glPopMatrix();
    }

    public void invalidate() {
        textureCache.clear();
        modelCache.clear();
    }

    protected Texture getTexture(Tile t) {
        Tileset.TileSprite sprite = t.getSprite();
        Tileset sheet = sprite.getTileset();
        Texture txt;
        if (textureCache.containsKey(sheet)) {
            txt = textureCache.get(sheet);
        } else {
            textureCache.put(sheet, txt = TextureLoader.getTexture(sheet.getImage(), GL_TEXTURE_2D, GL_RGBA));
        }
        return txt;
    }

    protected ModelScene getModel(TileModel t) {
        WavefrontObject m = t.getModel();
        ModelScene scene = null;
        if (m != null) {
            if (modelCache.containsKey(m)) {
                scene = modelCache.get(m);
            } else {
                try {
                    modelCache.put(m, scene = new ModelScene(m));
                } catch (IOException ex) {
                }
            }
        }
        return scene;
    }
}
