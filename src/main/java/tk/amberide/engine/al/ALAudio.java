package tk.amberide.engine.al;

import java.nio.IntBuffer;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.BufferUtils;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.OpenALException;

/**
 * A sound that can be played through OpenAL
 *
 * @author Kevin Glass
 * @author Nathan Sweet <misc@n4te.com>
 */
public class ALAudio implements Audio {

    protected int buffer;
    private int index = -1;
    private float length;
    private boolean loop;
    private float volume = 1f;
    private AudioFormat format;

    /**
     * Create a new sound
     *
     * @param store The sound store from which the sound was created
     * @param buffer The buffer containing the sound data
     */
    ALAudio(int buffer, AudioFormat format) {
        this.buffer = buffer;
        this.format = format;

        int bytes = alGetBufferi(buffer, AL_SIZE);
        int bits = alGetBufferi(buffer, AL_BITS);
        int channels = alGetBufferi(buffer, AL_CHANNELS);
        int freq = alGetBufferi(buffer, AL_FREQUENCY);

        length = ((bytes / (bits / 8)) / (float) freq) / channels;
    }

    /**
     * @see org.newdawn.slick.openal.Audio#isPlaying()
     */
    public State getState() {
        if (index != -1 && alGetSourcei(index, AL_SOURCE_STATE) == AL_PLAYING) {
            return State.PLAYING;
        } else if (index != -1) {
            return State.PAUSED;
        }
        return State.STOPPED;
    }

    public void start() {
        if (index != -1) {
            alSourcePlay(index);
        } else {
            int nextSource = findFreeSource();
            if (nextSource == -1) {
                index = -1;
                return;
            }

            index = getSource();
            alSourceStop(index);

            alSourcei(index, AL_BUFFER, buffer);
            alSourcef(index, AL_PITCH, 1);
            alSourcef(index, AL_GAIN, volume);
            alSourcei(index, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);

            alSourcePlay(index);
        }
    }

    public void pause() {
        if (index != -1) {
            alSourcePause(index);
        }
    }

    /**
     * @see org.newdawn.slick.openal.Audio#stop()
     */
    public void stop() {
        if (index != -1) {
            alSourceStop(index);
            index = -1;
        }
    }

    public void setVolume(float volume) {
        this.volume = volume;
        if (index != -1) {
            alSourcef(index, AL_PITCH, volume);
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setLooping(boolean flag) {
        loop = flag;
        if (index != -1) {
            alSourcei(index, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
        }
    }

    public boolean isLooping() {
        return loop;
    }

    public void setMillisecondPosition(long position) {
        alSourcei(index, AL11.AL_SEC_OFFSET, (int) ((position / 1000) % length));
    }

    public long getMillisecondPosition() {
        return alGetSourcei(index, AL11.AL_SEC_OFFSET) * 1000;
    }

    public long getMillisecondLength() {
        return (long) (length * 1000);
    }

    public AudioFormat getFormat() {
        return format;
    }

    private int findFreeSource() {
        for (int i = 1; i < 64 - 1; i++) {
            int state = alGetSourcei(getSource(), AL_SOURCE_STATE);

            if ((state != AL_PLAYING) && (state != AL_PAUSED)) {
                return i;
            }
        }

        return -1;
    }

    private int getSource() {
        IntBuffer temp = BufferUtils.createIntBuffer(1);
        try {
            alGenSources(temp);
            if (alGetError() == AL_NO_ERROR) {
                return temp.get(0);
            }
        } catch (OpenALException e) {
        }
        return -1;
    }
}
