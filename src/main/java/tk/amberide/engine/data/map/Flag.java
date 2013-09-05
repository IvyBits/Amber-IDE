package tk.amberide.engine.data.map;

import java.awt.Color;
import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author Tudor
 */
public class Flag {

    private int id;
    private String name;
    private Color color;
    protected static final TreeMap<Integer, Flag> flags = new TreeMap<Integer, Flag>();

    public static Collection<Flag> flags() {
        return flags.values();
    }
    
    public static Flag byId(int id) {
        return flags.get(id);
    }

    public static void registerFlag(Flag flag) {
        flags.put(flag.id, flag);
    }
    
    public static void unregisterFlag(int id) {
        flags.remove(id);
    }

    public Flag(Color color, String name, int id) {
        this.color = color;
        this.name = name;
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
