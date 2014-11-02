package tk.amberide.engine.data.map;

import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;

/**
 * @author Tudor
 */
public class Layer3D extends Layer {

    protected SparseVector<SparseMatrix<TileModel>> models = new SparseVector<SparseMatrix<TileModel>>();

    public Layer3D(String name, LevelMap map) {
        super(name, map);
    }

    public Layer3D clone() {
        Layer3D clone = new Layer3D(name, map);
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
            models.set(z, alt = new SparseMatrix<TileModel>(Math.max(map.getWidth(), map.getLength())));
        }
        alt.put(x, y, m);
    }

    public SparseVector<SparseMatrix<TileModel>> modelMatrix() {
        return models;
    }
}
