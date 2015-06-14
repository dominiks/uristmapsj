package org.uristmaps.data;

/**
 * Mutable version of the Coord2 class.
 * Created by dominik on 28.05.2015.
 */
public class Coord2Mutable extends Coord2 {

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord2 copyCoord2() {
        return new Coord2(x,y);
    }
}
