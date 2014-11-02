package tk.amberide.engine.renderer;

import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import tk.amberide.engine.data.map.Layer;
import tk.amberide.engine.data.map.LevelMap;
import tk.amberide.engine.data.map.Tile;
import tk.amberide.engine.data.map.TileModel;
import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;
import tk.amberide.engine.gl.tess.ITesselator;
import tk.amberide.engine.gl.tess.ImmediateTesselator;

/**
 *
 * @author Tudor
 */
public class GLMapRenderer3D {

    private final LevelMap map;
    private final ITesselator tess = new ImmediateTesselator();

    public GLMapRenderer3D(LevelMap map) {
        this.map = map;
    }

    public void render() {
        glEnable(GL_DEPTH_TEST);

        List<Layer> layers = map.getLayers();
        // Fix for z-buffer fighting
        glPolygonOffset(1, 1);

        for (int i = 0; i != layers.size(); i++) {
            drawLayer(layers.get(i));
        }
    }
    
    public void invalidate() {
        tess.invalidate();
    }
    
    public LevelMap getMap() {
        return map;
    }

    protected void drawLayer(Layer layer) {
        SparseVector<SparseMatrix<Tile>> tileVector = layer.tileMatrix();
        SparseVector<SparseMatrix<TileModel>> modelVector = layer instanceof Layer ? ((Layer) layer).modelMatrix()
                : new SparseVector<SparseMatrix<TileModel>>();
        tess.startTileBatch();
        SparseVector.SparseVectorIterator tileIterator = tileVector.iterator();
        while (tileIterator.hasNext()) {
            SparseMatrix.SparseMatrixIterator matrixIterator = ((SparseMatrix<Tile>) tileIterator.next()).iterator();
            while (matrixIterator.hasNext()) {
                Tile t = (Tile) matrixIterator.next();
                if (t != null) {
                    tess.drawTile(t, matrixIterator.realX(), matrixIterator.realY(), tileIterator.realIndex());
                }
            }
        }
        tess.endTileBatch();

        tess.startModelBatch();
        SparseVector.SparseVectorIterator modelIterator = modelVector.iterator();
        while (modelIterator.hasNext()) {
            SparseMatrix<TileModel> matrix = (SparseMatrix<TileModel>) modelIterator.next();
            SparseMatrix.SparseMatrixIterator matrixIterator = matrix.iterator();
            int z = modelIterator.realIndex();
            while (matrixIterator.hasNext()) {
                TileModel t = (TileModel) matrixIterator.next();
                if (t != null) {
                    tess.drawModel(t, matrixIterator.realX(), matrixIterator.realY(), z);
                }
            }
        }
        tess.endModelBatch();
    }
}
