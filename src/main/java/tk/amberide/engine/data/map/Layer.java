package tk.amberide.engine.data.map;

import tk.amberide.engine.data.sparse.SparseMatrix;
import tk.amberide.engine.data.sparse.SparseVector;

/**
 *
 * @author Tudor
 */
public class Layer implements Cloneable {
    protected SparseVector<SparseMatrix<Tile>> tiles = new SparseVector<SparseMatrix<Tile>>();
    protected SparseVector<SparseMatrix<Flag>> flags = new SparseVector<SparseMatrix<Flag>>();
    protected String name;
    protected final LevelMap map;

    public Layer(String name, LevelMap map) {
        this.name = name;
        this.map = map;
    }

    public Layer clone() {
        Layer clone = new Layer(name, map);
        clone.tiles = tiles.clone();
        clone.flags = flags.clone();
        return clone;
    }

    public Tile getTile(int x, int y, int z) {
        SparseMatrix<Tile> alt = tiles.get(z);
        return alt == null ? null : alt.get(x, y);
    }

    public void setTile(int x, int y, int z, Tile t) {
        SparseMatrix<Tile> alt = tiles.get(z);
        if (alt == null) {
            tiles.set(z, alt = new SparseMatrix<Tile>(Math.max(map.getWidth(), map.getLength())));
        }
        alt.put(x, y, t);
    }

    public Flag getFlag(int x, int y, int z) {
        SparseMatrix<Flag> alt = flags.get(z);
        return alt == null ? null : alt.get(x, y);
    }

    public void setFlag(int x, int y, int z, Flag f) {
        SparseMatrix<Flag> alt = flags.get(z);
        if (alt == null) {
            flags.set(z, alt = new SparseMatrix<Flag>(Math.max(map.getWidth(), map.getLength())));
        }
        alt.put(x, y, f);
    }

    public SparseVector<SparseMatrix<Tile>> tileMatrix() {
        return tiles;
    }

    public SparseVector<SparseMatrix<Flag>> flagMatrix() {
        return flags;
    }

    /**
     * @return the length
     */
    public int getHeight() {
        return tiles.nonZeroEntries();
    }

    public String getName() {
        return name; // Temporary
    }
}
