package tk.amberide.engine.gl.camera;

import tk.amberide.engine.input.AbstractKeyboard;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Tudor
 */
public class OrthographicCamera {

    private Vector2f vdelta = new Vector2f(0, 0);
    private Vector2f position;

    public OrthographicCamera() {
        this(0, 0);
    }

    public OrthographicCamera(int x, int y) {
        this(new Vector2f(x, y));
    }

    public OrthographicCamera(Vector2f position) {
        this.position = position;
    }

    public void processKeyboard(float delta) {
        processKeyboard(delta, 1, 1);
    }

    public void processKeyboard(float delta, float speed) {
        processKeyboard(delta, speed, speed);
    }

    public float x() {
        return position.x;
    }

    public float y() {
        return position.y;
    }

    public Vector2f position() {
        return position;
    }

    /**
     * Processes keyboard input and converts into camera movement.
     *
     * @param delta the elapsed time since the last frame update in milliseconds
     * @param speedX the speed of the movement on the x-axis (normal = 1.0)
     * @param speedY the speed of the movement on the y-axis (normal = 1.0)
     * @throws IllegalArgumentException if delta is 0 or delta is smaller than 0
     */
    public void processKeyboard(float delta, float speedX, float speedY) {
        if (delta <= 0) {
            throw new IllegalArgumentException("delta " + delta + " is 0 or is smaller than 0");
        }

        boolean keyUp = AbstractKeyboard.isKeyDown(Keyboard.KEY_UP) || AbstractKeyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = AbstractKeyboard.isKeyDown(Keyboard.KEY_DOWN) || AbstractKeyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyLeft = AbstractKeyboard.isKeyDown(Keyboard.KEY_LEFT) || AbstractKeyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyRight = AbstractKeyboard.isKeyDown(Keyboard.KEY_RIGHT) || AbstractKeyboard.isKeyDown(Keyboard.KEY_D);

        if (keyUp && !keyDown) {
            position.y += delta * speedY;
        } else if (keyDown && !keyUp) {
            position.y -= delta * speedY;
        }

        if (keyLeft && !keyRight) {
            position.x -= delta * speedX;
        } else if (keyRight && !keyLeft) {
            position.x += delta * speedX;
        }
    }

    public void applyTranslations() {
        glPushAttrib(GL_TRANSFORM_BIT);
        glMatrixMode(GL_MODELVIEW);
        glTranslatef(-position.x, -position.y, 0);
        glPopAttrib();
    }
}
