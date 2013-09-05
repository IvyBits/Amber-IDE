package tk.amberide.engine.gl.atlas;

import tk.amberide.engine.gl.Texture;
import tk.amberide.engine.gl.TextureLoader;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;

/**
 * A texture region based ITextureAtlas implementation. When a region is bound,
 * it generates a new texture from the subimage defined by the designated
 * coordinates, and binds it.
 *
 * @author Tudor
 */
public class ArrayTextureAtlas implements ITextureAtlas {

    protected HashMap<Rectangle, Texture> regions = new HashMap<Rectangle, Texture>();
    protected Texture bound;
    protected BufferedImage image;

    /**
     * Creates a new ArrayTextureAtlas.
     */
    public ArrayTextureAtlas(BufferedImage image) {
        this.image = image;
    }

    /**
     * @inheritDoc
     */
    public void bindTextureRegion(int x, int y, int w, int h) {
        Rectangle reg = new Rectangle(x, y, w, h);
        if (!regions.containsKey(reg)) {
            BufferedImage sub = image.getSubimage(x, y, w, h);
            if (!TextureAtlasFactory.isSupportedTextureSize(sub)) {
                throw new IllegalArgumentException("unsupported binding region size");
            }
            if (x + w > image.getWidth(null) || y + h > image.getHeight(null) || x < 0 || y < 0 || w < 0 || h < 0) {
                throw new IllegalArgumentException("invalid region coordinates");
            }
            regions.put(reg, TextureLoader.getTexture(sub));
        }
        (bound = regions.get(reg)).bind();
    }

    /**
     * @inheritDoc
     */
    public void unbind() {
        if (bound != null) {
            bound.unbind();
        }
    }

    /**
     * @inheritDoc
     */
    public void atlasCoord(float u, float v) {
        glTexCoord2f(u, v);
    }

    /**
     * @inheritDoc
     */
    public void invalidate() {
        regions.clear();
    }
}