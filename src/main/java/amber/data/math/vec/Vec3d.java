package amber.data.math.vec;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Tudor
 */
public class Vec3d extends Vector3f {

    public Vec3d(float x, float y, float z) {
        super(x, y, z);
    }

    public Vec3d() {
        super(0, 0, 0);
    }

    public Vec3d add(Vector3f other) {
        Vector3f.add(other, this, this);
        return this;
    }

    public Vec3d sub(Vector3f other) {
        Vector3f.sub(other, this, this);
        return this;
    }

    public Vec3d multiply(Vec3d other) {
        x *= other.x;
        y *= other.y;
        z *= other.z;
        return this;
    }

    public float dot(Vector3f other) {
        return Vector3f.dot(other, this);
    }

    public float l2Norm() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3d crossAndAssign(Vector3f a, Vector3f b) {
        float tempX = a.y * b.z - a.z * b.y;
        float tempY = a.z * b.x - a.x * b.z;
        float tempZ = a.x * b.y - a.y * b.x;

        x = tempX;
        y = tempY;
        z = tempZ;

        return this;
    }

    public Vec3d floor() {
        x = (int) x;
        y = (int) y;
        z = (int) z;
        return this;
    }

    public Vec3d scale(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;

        return this;
    }

    public Vec3d normalize() {
        float length = l2Norm();
        x /= length;
        y /= length;
        z /= length;

        return this;
    }
}
