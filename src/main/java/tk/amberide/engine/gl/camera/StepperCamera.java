package tk.amberide.engine.gl.camera;

import tk.amberide.engine.data.map.Direction;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Tudor
 */
public class StepperCamera {

    private boolean rotateDirection, rotate;
    private float zoom, step;

    public Direction getFacingDirection() {
        return Direction.NORTH;
    }
    
    public void processMouse() {
        float zoomModifier = -Mouse.getDWheel() / 12000f;
        if (zoomModifier < 0) {
            if (zoom + zoomModifier > 0.15f) {
                zoom += zoomModifier;
            }
        } else if (zoomModifier > 0) {
            zoom += zoomModifier;
        }
    }

    public void processKeyboard() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            rotateDirection = false;
            rotate = true;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            rotateDirection = true;
            rotate = true;
        } else {
            rotate = false;
        }

        if (rotate) {
            if (rotateDirection == /* left */ false) {
                step -= 0.03f;
            } else if (rotateDirection == /* right */ true) {
                step += 0.03f;
            }
        }
    }

    public void applyTranslations() {
        GLU.gluLookAt((float) Math.sin(step) * 3 * zoom, 0, (float) Math.cos(step) * 3 * zoom, 0, 0, 0, 0, 1, 0);
    }
}
