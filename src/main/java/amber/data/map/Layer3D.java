package amber.data.map;

import amber.data.sparse.SparseMatrix;
import amber.data.sparse.SparseVector;
import amber.gl.model.obj.WavefrontObject;

/**
 *
 * @author Tudor
 */
public class Layer3D extends Layer {

    protected SparseVector<SparseMatrix<TileModel>> models = new SparseVector<SparseMatrix<TileModel>>();

    public Layer3D(String name, int width, int length) {
        super(name, width, length);
    }

    public Layer3D clone() {
        Layer3D clone = new Layer3D(name, width, length);
        clone.tiles = tiles.clone();
        clone.models = models.clone();
        return clone;
    }

    public TileModel getModel(int x, int y, int z) {
        SparseMatrix<TileModel> alt = models.get(z);
        return alt == null ? null : alt.get(x, y);
    }

    public void setModel(int x, int y, int z, TileModel m) {
        SparseMatrix<TileModel> alt = models.get(z);
        if (alt == null) {
            models.set(z, alt = new SparseMatrix<TileModel>(Math.max(width, length)));
        }
        alt.put(x, y, m);
    }

    public SparseVector<SparseMatrix<TileModel>> modelMatrix() {
        return models;
    }
}
