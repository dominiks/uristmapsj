package org.uristmaps.data;

/**
 * Object containing an X and Y coordinate. Can be used as a key in maps.
 */
public class Coord2d {

    protected double x;
    protected double y;

    public Coord2d() {}

    /**
     * Create a new value pair.
     * @param x
     * @param y
     */
    public Coord2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x value.
     * @return
     */
    public double X() {
        return x;
    }

    /**
     * Get the y value.
     * @return
     */
    public double Y() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coord2d)) {
            return false;
        }

        Coord2d other = (Coord2d) obj;
        return (x == other.x && y == other.y);
    }

    @Override
    public int hashCode() {
        return (int) (31 * x + y);
    }
}
