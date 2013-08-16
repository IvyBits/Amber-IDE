package amber.gl;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * A class for OpenGL boilerplate
 *
 * @author Tudor
 */
public class GLE {

    public static void gleLine(float x, float y, float z, float x1, float y1, float z1) {
        glVertex3f(x, y, z);
        glVertex3f(x1, y1, z1);
    }

    public static void gleLine2d(float x, float y, float x1, float y1) {
        glVertex2f(x, y);
        glVertex2f(x1, y1);
    }

    public static void gleRect2d(float x, float z, float width, float height) {
        float x2 = x + width;
        float z2 = z + height;
        glBegin(GL_LINE_LOOP);
        {
            glVertex2f(x, z);
            glVertex2f(x2, z);
            glVertex2f(x2, z2);
            glVertex2f(x, z2);
        }
        glEnd();
    }

    public static void gleColor(Color color) {
        glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void gleClearColor(Color color) {
        glClearColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static void glePushOrthogonalMode(float x, float width, float y, float height) {
        //GL11.glDisable(GL11.GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);                        // Select The Projection Matrix
        glPushMatrix();                                     // Store The Projection Matrix
        glLoadIdentity();                                   // Reset The Projection Matrix
        glOrtho(x, width, y, height, -1, 1);                          // Set Up An Ortho Screen
        glMatrixMode(GL_MODELVIEW);                         // Select The Modelview Matrix
        glPushMatrix();                                     // Store The Modelview Matrix
        glLoadIdentity();                                   // Reset The Modelview Matrix
    }

    public static void glePushFrustrumMode() {
        glMatrixMode(GL_PROJECTION);                        // Select The Projection Matrix
        glPopMatrix();                                      // Restore The Old Projection Matrix
        glMatrixMode(GL_MODELVIEW);                         // Select The Modelview Matrix
        glPopMatrix();                                      // Restore The Old Projection Matrix
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void gleToggleWireframe() {
        switch (glGetInteger(GL_POLYGON_MODE)) {
            case GL_LINE:
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                break;
            case GL_FILL:
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                break;
        }
    }
}
