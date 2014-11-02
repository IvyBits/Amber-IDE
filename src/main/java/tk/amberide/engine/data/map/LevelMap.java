package tk.amberide.engine.data.map;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tudor
 */
public class LevelMap implements Cloneable {

    public static final short MAP_VERSION = 0x01;
    private LinkedList<Layer> layers = new LinkedList<Layer>();
    private int width, length;

    public LevelMap(int width, int length) {
        this.width = width;
        this.length = length;
    }

    public LevelMap clone() {
        LevelMap clone = new LevelMap(width, length);
        clone.layers = new LinkedList<Layer>();
        for (int i = 0; i != layers.size(); i++) {
            clone.layers.add(layers.get(i).clone());
        }
        return clone;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public Layer getLayer(int layer) {
        return layers.get(layer);
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public boolean removeLayer(Layer layer) {
        return layers.remove(layer);
    }

    public Layer removeLayer(int index) {
        return layers.remove(index);
    }

    public void insertLayer(int index, Layer layer) {
        layers.add(index, layer);
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "LevelMap{" +
                "width=" + width +
                ", length=" + length +
                '}';
    }
}
