package tk.amberide.engine.data.math;

import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author xiaomao5
 * @author Tudor
 */
public class Angles {

    public static Vector2f circleIntercept(float theta, float originX, float originY, int len) {
        theta = theta % 360;
        double rad = Math.toRadians(theta % 90);
        if (theta % 90 / 45 >= 1) {
            rad = Math.PI / 2 - rad;
        }
        float tan = (float) Math.tan(rad) * len;

        switch ((int) theta / 45) {
            case 2:
            case 4:
            case 5:
            case 7:
                tan = -tan;
        }

        boolean horizontal = (theta + 45) % 180 / 90 <= 1;
        float delta = ((theta + 45) % 360 / 180 < 1) ? len : -len;

        return horizontal ? new Vector2f(originX + delta, originY + tan) : new Vector2f(originX + tan, originY + delta);
    }

    public static void main(String[] args) {
        for (int i = 0; i != 360; i += 15) {
            System.out.print(i + ": ");
            Vector2f point = circleIntercept(i, 0, 0, 2);
            System.out.println(point.x + ", " + point.y);
        }
    }
}
