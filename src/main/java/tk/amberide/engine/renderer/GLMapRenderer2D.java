package tk.amberide.engine.renderer;

import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import tk.amberide.engine.data.map.Layer;
import tk.amberide.engine.data.map.LevelMap;
import tk.amberide.engine.data.map.Tile;
import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;
import tk.amberide.engine.gl.tess.ITesselator;
import tk.amberide.engine.gl.tess.ImmediateTesselator;

/**
 *
 * @author Tudor
 */
public class GLMapRenderer2D {

    private final ITesselator tess = new ImmediateTesselator();
    private final LevelMap map;

    public GLMapRenderer2D(LevelMap map) {
        this.map = map;
    }

    public void invalidate() {
        tess.invalidate();
    }

    public LevelMap getMap() {
        return map;
    }

    public void render() {
        glPushMatrix();
        glTranslatef(1, 0, 0);
        List<Layer> layers = map.getLayers();
        for (int i = 0; i != layers.size(); i++) {
            drawLayer(layers.get(i));
        }
        glPopMatrix();    }

    protected void drawLayer(Layer layer) {
        tess.startTileBatch();
        SparseVector.SparseVectorIterator tileIterator = layer.tileMatrix().iterator();
        while (tileIterator.hasNext()) {
            SparseMatrix.SparseMatrixIterator matrixIterator = ((SparseMatrix<Tile>) tileIterator.next()).iterator();
            while (matrixIterator.hasNext()) {
                Tile t = (Tile) matrixIterator.next();
                if (t != null) {
                    tess.drawTile2D(t, matrixIterator.realX(), matrixIterator.realY());
                }
            }
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        tess.endTileBatch();
    }
}
