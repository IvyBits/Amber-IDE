package tk.amberide.engine.gl.atlas;

import tk.amberide.engine.gl.Texture;
import tk.amberide.engine.gl.TextureLoader;

import java.awt.image.BufferedImage;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.ARBTextureRectangle.*;

/**
 * An ITextureAtlas implementation using the GL_TEXTURE_RECTANGLE_ARB extension.
 *
 * @author Tudor
 */
public class RectangleTextureAtlas implements ITextureAtlas {

    protected Texture tex;
    protected int bound = 0;
    protected BufferedImage image;
    protected int x, y, w, h;

    public RectangleTextureAtlas(BufferedImage image) {
        if (!GLContext.getCapabilities().GL_ARB_texture_rectangle) {
            throw new IllegalStateException("texture rectangles not supported");
        }
        if (!TextureAtlasFactory.isSupportedTextureSize(image)) {
            throw new IllegalStateException("unsupported texture size, max is " + glGetInteger(GL_MAX_TEXTURE_SIZE));
        }
        this.image = image;
    }

    /**
     * @inheritDoc
     */
    public void bindTextureRegion(int x, int y, int w, int h) {
        glEnable(GL_TEXTURE_RECTANGLE_ARB);
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        if (tex == null) {
            tex = TextureLoader.getTexture(image);
        }
        if (bound != tex.getID()) {
            tex.bind();
            bound = tex.getID();
        }
    }

    /**
     * @inheritDoc
     */
    public void unbind() {
        if (tex != null) {
            tex.unbind();
            bound = 0;
        }
        glDisable(GL_TEXTURE_RECTANGLE_ARB);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * @inheritDoc
     */
    public void atlasCoord(float u, float v) {
        // bindTextureRegion set the linear rectangle coordinates, now we must convert
        // the normalized u, v values to rectangle coordinates.
        glTexCoord2f(x + w * u, y + v * h);
    }

    /**
     * @inheritDoc
     */
    public void invalidate() {
        tex = null;
        bound = 0;
    }
}