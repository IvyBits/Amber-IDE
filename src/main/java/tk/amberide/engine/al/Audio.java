package tk.amberide.engine.al;

import javax.sound.sampled.AudioFormat;

public interface Audio {

    public enum State {

        PLAYING, PAUSED, STOPPED
    }

    void start();

    void pause();

    void stop();

    State getState();

    void setVolume(float volume);

    float getVolume();

    void setLooping(boolean flag);

    boolean isLooping();

    void setMillisecondPosition(long position);

    long getMillisecondPosition();

    long getMillisecondLength();

    AudioFormat getFormat();
}