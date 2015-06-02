package org.uristmaps.data;

/**
 * Object containing an X and Y coordinate. Can be used as a key in maps.
 */
public class Coord2 {

    protected int x;
    protected int y;

    public Coord2() {}

    /**
     * Create a new value pair.
     * @param x
     * @param y
     */
    public Coord2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x value.
     * @return
     */
    public int X() {
        return x;
    }

    /**
     * Get the y value.
     * @return
     */
    public int Y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coord2)) {
            return false;
        }

        Coord2 other = (Coord2) obj;
        return (x == other.x && y == other.y);
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return X() + "," + Y();
    }
}
