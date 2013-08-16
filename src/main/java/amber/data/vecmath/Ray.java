package amber.data.vecmath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetFloat;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.util.glu.GLU.gluUnProject;

/**
 *
 * @author Tudor
 */
public class Ray {

    public Vec3d point;
    public Vec3d dir;

    public Ray(Vec3d point, Vec3d dir) {
        this.point = point;
        this.dir = dir;
    }

    public static Ray getRay(float cursorX, float cursorY) {
        IntBuffer viewport = ByteBuffer.allocateDirect((Integer.SIZE / 8) * 16).order(ByteOrder.nativeOrder()).asIntBuffer();
        FloatBuffer modelview = ByteBuffer.allocateDirect((Float.SIZE / 8) * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer projection = ByteBuffer.allocateDirect((Float.SIZE / 8) * 16).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer pickingRayBuffer = ByteBuffer.allocateDirect((Float.SIZE / 8) * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glGetFloat(GL_MODELVIEW_MATRIX, modelview);
        glGetFloat(GL_PROJECTION_MATRIX, projection);
        glGetInteger(GL_VIEWPORT, viewport);

        float winX = (float) cursorX;
        // convert window coordinates to opengl coordinates (top left to bottom left for (0,0)
        float winY = (float) viewport.get(3) - (float) cursorY;

        // now unproject this to get the  vector in to the screen
        // take the frustrm and unproject in to the screen
        // frustrum has a near plane and a far plane

        // first the near vector
        gluUnProject(winX, winY, 0, modelview, projection, viewport, pickingRayBuffer);
        Vec3d nearVector = new Vec3d(pickingRayBuffer.get(0), pickingRayBuffer.get(1), pickingRayBuffer.get(2));

        pickingRayBuffer.rewind();
        // now the far vector
        gluUnProject(winX, winY, 1, modelview, projection, viewport, pickingRayBuffer);
        Vec3d farVector = new Vec3d(pickingRayBuffer.get(0), pickingRayBuffer.get(1), pickingRayBuffer.get(2));

        //save the results in a vector, far-near
        return new Ray(nearVector, farVector.sub(nearVector));
    }
}