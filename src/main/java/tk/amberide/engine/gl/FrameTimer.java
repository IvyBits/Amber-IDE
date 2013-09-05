package tk.amberide.engine.gl;

import org.lwjgl.Sys;

/**
 *
 * @author Tudor
 */
public class FrameTimer {

    private long lastFrame;
    private long lastFPS;
    private int fps;
    private int frames;

    public void start() {
        getDelta();
        lastFPS = getTime();
    }

    public int fps() {
        return fps;
    }

    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Calculate the FPS and set it in the title bar
     */
    public void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            fps = frames;
            frames = 0; //reset the frames counter
            lastFPS += 1000; //add one second
        }
        frames++;
    }
}