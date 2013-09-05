package tk.amberide.engine.data.map;

import tk.amberide.engine.gl.model.obj.WavefrontObject;

/**
 *
 * @author Tudor
 */
public class TileModel {

    protected WavefrontObject model;

    public TileModel(WavefrontObject model) {
        this.model = model;
    }

    public WavefrontObject getModel() {
        return model;
    }
}
