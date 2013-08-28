package amber.gui.editor.map.res;

import amber.data.io.ImagingTools;
import amber.gl.GLUtil;
import amber.gl.model.ModelScene;
import amber.gl.model.obj.WavefrontObject;
import amber.gui.misc.ErrorHandler;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.WeakHashMap;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Tudor
 */
public class ModelThumbnail {

    private static final WeakHashMap<WavefrontObject, Image> thumbs = new WeakHashMap<WavefrontObject, Image>();

    public static Image getModelImage(WavefrontObject obj) {
        if (!thumbs.containsKey(obj)) {
            thumbs.put(obj, makeImage(obj, 512, 512));
        }
        return thumbs.get(obj);
    }

    public static Image getModelImage(WavefrontObject obj, int w, int h) {
        Image img = getModelImage(obj);
        if (img.getHeight(null) > h || img.getWidth(null) > w) {
            return ImagingTools.scaleImage((BufferedImage) img, w, h);
        }
        return img;
    }

    public static void clearCache() {
        thumbs.clear();
    }

    private static Image makeImage(final WavefrontObject model, final int twidth, final int theight) {
        final int width = 512;
        final int height = 512;
        BufferedImage img = null;
        try {
            img = GLUtil.renderImage(
                    width,
                    height,
                    new Runnable() {
                @Override
                public void run() {
                    glClearColor(0, 0, 0, 0);
                    ModelScene scene;
                    try {
                        scene = new ModelScene(model);
                    } catch (IOException ex) {
                        ErrorHandler.alert(ex);
                        return;
                    }

                    // Enable transperancy              
                    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();

                    glEnable(GL_BLEND);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                    glShadeModel(GL_SMOOTH);
                    glEnable(GL_DEPTH_TEST);

                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    float fAspect = (float) width / (float) height;
                    GLU.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);

                    for (int i = 0; i != 2; i++) {
                        glMatrixMode(GL_MODELVIEW);
                        glLoadIdentity();
                        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                        glLoadIdentity();
                        GLU.gluLookAt((float) Math.sin(0) * 3 * 5, 0, (float) Math.cos(0) * 3 * 5, 0, 0, 0, 0, 1, 0);
                        glTranslatef(0, -3, 0);
                        scene.draw();
                    }
                }
            });

            if (img != null) {
                return ImagingTools.trimAlpha(img);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return img;
    }
}
