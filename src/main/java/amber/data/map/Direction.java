package amber.data.map;

/**
 *
 * @author Tudor
 */
public enum Direction {

    NORTH,
    EAST,
    SOUTH,
    WEST,
    NORTH_EAST(NORTH),
    NORTH_WEST(NORTH),
    SOUTH_EAST(SOUTH),
    SOUTH_WEST(SOUTH);
    private Direction parent;

    Direction() {
        this(null);
    }

    Direction(Direction parent) {
        this.parent = parent;
    }
    
    public boolean cardinal() {
        return parent == null;
    }
    
    public Direction toCardinal() {
        return parent == null ? this : parent;
    }
}