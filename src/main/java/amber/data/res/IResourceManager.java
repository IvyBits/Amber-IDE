package amber.data.res;

import amber.al.Audio;
import amber.gl.model.obj.WavefrontObject;
import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Tudor
 */
public interface IResourceManager {

    void loadResources() throws Exception;

    void emitResources() throws Exception;

    void importTileset(String name, Tileset sheet, File source);

    void removeTileset(String name);

    void importAudio(String name, Audio clip, File source);

    void removeAudio(String name);

    void importModel(String name, WavefrontObject model, File source);

    void removeModel(String name);

    Tileset getTileset(String name);

    Audio getAudio(String name);

    WavefrontObject getModel(String name);

    Resource<Tileset> getTilesetResource(String name);

    Resource<Audio> getAudioResource(String name);

    Resource<WavefrontObject> getModelResource(String name);

    Collection<Resource<Tileset>> getTilesets();

    Collection<Resource<Audio>> getClips();

    Collection<Resource<WavefrontObject>> getModels();

    Collection<IResourceListener> getResourceListeners();

    void addResourceListener(IResourceListener listener);

    void removeResourceListener(IResourceListener listener);

    void registerResourceListener(Object listener);

    void unregisterResourceListener(Object listener);
}
