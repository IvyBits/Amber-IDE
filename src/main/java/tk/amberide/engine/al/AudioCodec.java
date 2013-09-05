package tk.amberide.engine.al;

import java.io.InputStream;

/**
 *
 * @author Tudor
 */
public interface AudioCodec {

    Audio readAudio(InputStream in) throws Exception;

}
