package tk.amberide.engine.data.map.codec;

import tk.amberide.engine.data.map.LevelMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tudor
 */
public abstract class Codec {

    public static final byte CURRENT_VERSION = 0x01;
    public static Map<Byte, Codec> CODECS = new HashMap<Byte, Codec>();

    static {
        CODECS.put((byte) 0x01, new V1());
    }

    public static Codec getLatestCodec() {
        return getCodec(CURRENT_VERSION);
    }

    public static Codec getCodec(byte version) {
        return CODECS.get(version);
    }

    public abstract void compileMap(LevelMap map, DataOutputStream out) throws IOException;

    public abstract LevelMap loadMap(DataInputStream in) throws IOException;
}
