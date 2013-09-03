package amber.gl.atlas;

import java.awt.Image;
import java.awt.image.BufferedImage;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.opengl.GL11.*;

/**
 * A factory that maps Images to ITextureAtlas implementations.
 *
 * @author Tudor
 */
public class TextureAtlasFactory {

    /**
     * Creates a texture atlas from the given image.
     *
     * @param img an image to map to an atlas
     * @return the generated texture atlas
     */
    public static ITextureAtlas createAtlas(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image must not be null");
        }

        // The ArrayTextureAtlas does not bind the entire image, so max size is irrelevant.
        // If the OpenGL context supports texture rectangles, RectangleTextureAtlas will provide
        // much better performance.
        if (GLContext.getCapabilities().GL_ARB_texture_rectangle && isSupportedTextureSize(image)) {
            return new RectangleTextureAtlas(image);
        }
        return new ArrayTextureAtlas(image);
    }

    /**
     * Checks if an image is supported natively by OpenGL.
     *
     * @param img the image to check
     * @return whether the image is supported to be turned into a texture
     */
    public static boolean isSupportedTextureSize(Image img) {
        int max = glGetInteger(GL_MAX_TEXTURE_SIZE);
        return img.getWidth(null) <= max  || img.getHeight(null) <= max;
    }
}