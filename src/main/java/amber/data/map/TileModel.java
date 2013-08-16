package amber.data.map;

import amber.gl.model.obj.WavefrontObject;

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
