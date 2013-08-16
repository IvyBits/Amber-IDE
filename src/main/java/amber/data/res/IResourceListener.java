package amber.data.res;

import amber.al.Audio;
import amber.gl.model.obj.WavefrontObject;

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