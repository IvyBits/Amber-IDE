package tk.amberide.engine.gl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author Tudor
 */
public class GLUtil {

    private static final Object PBUFFER_LOCK = new Object();

    public static BufferedImage renderImage(int width, int height, Runnable renderer) throws LWJGLException, IOException {

        synchronized (PBUFFER_LOCK) {
            ContextAttribs contextAtrributes = new ContextAttribs(1, 1);
            contextAtrributes.withForwardCompatible(true);

            Pbuffer pbuffer = new Pbuffer(width, height, new PixelFormat(), null, null, contextAtrributes);
            pbuffer.makeCurrent();
            if (pbuffer.isBufferLost()) {
                pbuffer.destroy();
                return null;
            }
            IntBuffer pixels = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            renderer.run();

            glReadPixels(0, 0, width, height, GL_BGRA, GL_UNSIGNED_BYTE, pixels);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bi.setRGB(x, y, pixels.get((height - y - 1) * width + x));
                }
            }

            pbuffer.destroy();
            return bi;
        }
    }
}