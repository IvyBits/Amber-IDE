package tk.amberide.ide.data.res;

import tk.amberide.engine.al.Audio;
import tk.amberide.engine.gl.model.obj.WavefrontObject;

/**
 *
 * @author Tudor
 */
public interface IResourceListener {

    void tilesetImported(Resource<Tileset> sheet);

    void tilesetRemoved(Resource<Tileset> sheet);

    void audioImported(Resource<Audio> clip);

    void audioRemoved(Resource<Audio> clip);

    void modelImported(Resource<WavefrontObject> model);

    void modelRemoved(Resource<WavefrontObject> model);
}