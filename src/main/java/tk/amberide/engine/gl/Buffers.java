package tk.amberide.engine.gl;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Buffers {
    public static FloatBuffer asFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        return buffer;
    }

    public static FloatBuffer asFlippedFloatBuffer(float... values) {
        FloatBuffer buffer = asFloatBuffer(values);
        buffer.flip();
        return buffer;
    }
}
