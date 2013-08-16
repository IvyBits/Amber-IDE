package amber.gl;

import org.lwjgl.BufferUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL12;

/**
 * A utility class to load textures for OpenGL. This source is based on a
 * texture that can be found in the Java Gaming (www.javagaming.org) Wiki. It
 * has been simplified slightly for explicit 2D graphics use.
 * <p/>
 * OpenGL uses a particular image format. Since the images that are loaded from
 * disk may not match this format this loader introduces a intermediate image
 * which the source image is copied into. In turn, this image is used as source
 * for the OpenGL texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureLoader {

    private static ColorModel glAlphaColorModel = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 8},
            true,
            false,
            ComponentColorModel.TRANSLUCENT,
            DataBuffer.TYPE_BYTE);
    private static ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[]{8, 8, 8, 0},
            false,
            false,
            ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);
    private static IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(5);

    private static int createTextureID() {
        glGenTextures(textureIDBuffer);
        return textureIDBuffer.get(0);
    }

    /**
     * Load a texture
     *
     * @param resourceName The location of the resource to load
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    public static Texture getTexture(String resourceName) {
        Texture tex = null;


        tex = getTexture(resourceName,
                GL_TEXTURE_2D, // target
                GL_RGBA);


        return tex;
    }

    public static Texture getTexture(File resourceName) {
        Texture tex = null;


        tex = getTexture(loadImage(resourceName),
                GL_TEXTURE_2D, // target
                GL_RGBA);


        return tex;
    }

    public static Texture getTexture(BufferedImage img,
            int target,
            int dstPixelFormat) {
        int srcPixelFormat;

        // create the texture ID for this texture
        int textureID = createTextureID();
        Texture texture = new Texture(target, textureID, img.getColorModel().hasAlpha());

        // bind this texture
        glBindTexture(target, textureID);

        texture.setWidth(img.getWidth());
        texture.setHeight(img.getHeight());

        if (texture.hasAlpha()) {
            srcPixelFormat = GL_RGBA;
        } else {
            srcPixelFormat = GL_RGB;
        }

        // convert that image into a byte buffer of texture data
        ByteBuffer textureBuffer = convertImageData(img, texture);

        if (target == GL_TEXTURE_2D) {
            glTexParameteri(target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        }

        // produce a texture from the byte buffer
        glTexImage2D(target,
                0,
                dstPixelFormat,
                get2Fold(img.getWidth()),
                get2Fold(img.getHeight()),
                0,
                srcPixelFormat,
                GL_UNSIGNED_BYTE,
                textureBuffer);

        return texture;
    }

    /**
     * Load a texture into OpenGL from a image reference on disk.
     *
     * @param resourceName The location of the resource to load
     * @param target The GL target to load the texture against
     * @param dstPixelFormat The pixel format of the screen
     * @return The loaded texture
     * @throws IOException Indicates a failure to access the resource
     */
    public static Texture getTexture(String resourceName,
            int target,
            int dstPixelFormat) {
        return getTexture(loadImage(resourceName), target, dstPixelFormat);
    }

    public static Texture getTexture(BufferedImage img) {
        return getTexture(img, GL_TEXTURE_2D, // target
                GL_RGBA);
    }

    private static int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret <<= 1;
        }
        return ret;
    }

    /**
     * Convert the buffered image to a texture
     *
     * @param bufferedImage The image to convert to a texture
     * @param texture The texture to store the data into
     * @return A buffer containing the data
     */
    public static ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) {
        ByteBuffer imageBuffer;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = get2Fold(bufferedImage.getWidth());
        int texHeight = get2Fold(bufferedImage.getHeight());

        texture.setTextureHeight(texHeight);
        texture.setTextureWidth(texWidth);

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
        }

        // copy the source image into the produced image
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0F, 0F, 0F, 0F));
        g.fillRect(0, 0, texWidth, texHeight);
        g.drawImage(bufferedImage, 0, 0, null);
        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    /**
     * Load a given resource as a buffered image
     *
     * @param ref The location of the resource to load
     * @return The loaded buffered image
     * @throws IOException Indicates a failure to find a resource
     */
    public static BufferedImage loadImage(String ref) {
        // due to an issue with ImageIO and mixed signed code
        // we are now using good old fashioned ImageIcon to load
        // images and the paint it on top of a new BufferedImage
        Image img;
        try {
            img = new ImageIcon(ImageIO.read(ClassLoader.getSystemResourceAsStream(ref))).getImage();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }

    public static BufferedImage loadImage(File ref) {
        Image img = new ImageIcon(ref.getAbsolutePath()).getImage();
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return bufferedImage;
    }
}