package tk.amberide.engine.al;

import tk.amberide.ide.gui.misc.ErrorHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.util.WaveData;

public class AudioIO extends Object {

    private static final HashMap<String, AudioCodec> codecs = new HashMap<String, AudioCodec>();
    private static boolean al;
    public static final AudioCodec CODEC_AIF = new AudioCodec() {
        public Audio readAudio(InputStream in) throws Exception {
            if (!al) {
                return CODEC_JAVASOUND.readAudio(in);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            AudioFormat af = ais.getFormat();
            int buffer = -1;
            try {
                IntBuffer buf = BufferUtils.createIntBuffer(1);

                AiffDecoder data = AiffDecoder.create(ais);
                alGenBuffers(buf);
                alBufferData(buf.get(0), data.format, data.data, data.samplerate);

                buffer = buf.get(0);
            } catch (Exception e) {
                ErrorHandler.alert(e);
            }

            if (buffer == -1) {
                throw new IOException("unable to load: " + in);
            }

            ALAudio ala = new ALAudio(buffer, af);
            return ala;
        }
    };
    public static final AudioCodec CODEC_WAV = new AudioCodec() {
        public Audio readAudio(InputStream in) throws Exception {
            if (!al) {
                return CODEC_JAVASOUND.readAudio(in);
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(in);
            AudioFormat af = ais.getFormat();

            int buffer = -1;
            try {
                IntBuffer buf = BufferUtils.createIntBuffer(1);

                WaveData data = WaveData.create(ais);
                alGenBuffers(buf);
                alBufferData(buf.get(0),
                        data.format,
                        data.data,
                        data.samplerate);

                buffer = buf.get(0);
            } catch (Exception e) {
                ErrorHandler.alert(e);
            }

            if (buffer == -1) {
                throw new IOException("unable to load: " + in);
            }

            ALAudio ala = new ALAudio(buffer, af);
            return ala;
        }
    };
    public static final AudioCodec CODEC_OGG = new AudioCodec() {
        public Audio readAudio(InputStream in) throws Exception {
            if (!al) {
                throw new IllegalStateException("cannot get ogg audio: AL context unavailable");
            }

            BufferedInputStream bais = new BufferedInputStream(in);
            int buffer = -1;
            AudioFileFormat format = null;
            try {
                IntBuffer buf = BufferUtils.createIntBuffer(1);

                OggDecoder decoder = new OggDecoder(bais);

                alGenBuffers(buf);
                alBufferData(buf.get(0), decoder.channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, decoder.data, decoder.rate);

                OggDecoder.Stream aux = decoder.oggInput;
                buffer = buf.get(0);

                AudioFormat base = new AudioFormat(aux.getRate(), 16, alGetBufferi(buffer, AL_CHANNELS), true, aux.bigEndian);
                format = new AudioFileFormat(
                        new AudioFileFormat.Type("OGG", "ogg"),
                        base, (int) (base.getFrameRate()
                        * (((alGetBufferi(buffer, AL_SIZE)
                        / (alGetBufferi(buffer, AL_BITS) / 8))
                        / (float) alGetBufferi(buffer, AL_FREQUENCY))
                        / alGetBufferi(buffer, AL_CHANNELS)
                        / 1000000 + 4)));
            } catch (Exception e) {
                ErrorHandler.alert(e);
            }

            if (buffer == -1) {
                throw new IOException("unable to load: " + in);
            }
            ALAudio ala = new ALAudio(buffer, format.getFormat());
            return ala;
        }
    };
    public static final AudioCodec CODEC_MIDI = new AudioCodec() {
        public MidiAudio readAudio(InputStream in) throws Exception {
            return new MidiAudio(in);
        }
    };
    public static final AudioCodec CODEC_JAVASOUND = new AudioCodec() {
        public JSAudio readAudio(InputStream in) throws Exception {
            return new JSAudio(AudioSystem.getAudioInputStream(in));
        }
    };

    static {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    AL.create();
                } catch (Exception e) {
                    ErrorHandler.alert(e);
                }
                al = AL.isCreated();
                return null;
            }
        });
        codecs.put("AIF", CODEC_AIF);
        codecs.put("WAV", CODEC_WAV);
        codecs.put("OGG", CODEC_OGG);
        codecs.put("MIDI", CODEC_MIDI);
        codecs.put("MID", CODEC_MIDI);
    }

    public static Audio read(File file) throws Exception {
        String fileName = file.getName();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > fileName.lastIndexOf(File.pathSeparator)) {
            extension = fileName.substring(i + 1);
        }

        return read(extension, new BufferedInputStream(new FileInputStream(file)));
    }

    public static Audio read(String format, InputStream in) throws Exception {
        format = format.toUpperCase();
        if (codecs.containsKey(format)) {
            return codecs.get(format).readAudio(in);
        }

        throw new IOException("unsupported format for Audio: " + format);
    }

    public static AudioCodec getCodec(String format) {
        return codecs.get(format.toUpperCase());
    }

    public static void addCodec(String format, AudioCodec codec) {
        codecs.put(format.toUpperCase(), codec);
    }

    public static Mixer findMixer(AudioFormat format) {
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class,
                format);
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        //check each available mixer to see if it is acceptable
        for (int i = 0; i < mixerInfos.length; i++) {
            Mixer mixer = AudioSystem.getMixer(mixerInfos[i]);
            //first check if it supports our line
            if (!mixer.isLineSupported(lineInfo)) {
                continue; //nope
            }
            //now check if we've used up our lines
            int maxLines = mixer.getMaxLines(lineInfo);
            //if it's not specified, it's supposedly unlimited
            if (maxLines == AudioSystem.NOT_SPECIFIED) {
                return mixer;
            }
            //otherwise we should count them
            int linesOpen = 0;
            Line[] sourceLines = mixer.getSourceLines();
            for (int s = 0; s < sourceLines.length; s++) {
                //check if it matches our line
                if (sourceLines[s].getLineInfo().matches(lineInfo)) {
                    linesOpen++; //one line used up
                }
            }
            //now we can see if any are available
            if (maxLines > linesOpen) {
                return mixer;
            }
        }
        //couldn't find one
        return null;
    }

    public static AudioFormat getFormat(InputStream in) {
        try {
            AudioFormat af = AudioSystem.getAudioInputStream(new BufferedInputStream(in)).getFormat();
            System.out.println(af);
            return af;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
