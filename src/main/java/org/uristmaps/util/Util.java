package org.uristmaps.util;

import org.uristmaps.data.Coord2;

/**
 * Some utility stuff that gathered for the project.
 */
public class Util {

    /**
     * Transform world tile to unit coordinates.
     * The site-coordinates are specified in world tiles which are 16x16 units big.
     * @param world
     * @return The top left unit-coordinate of the world tile.
     */
    public static Coord2 worldToUnit(Coord2 world) {
        return new Coord2(world.X() * 16, world.Y() * 16);
    }

    /**
     * Converts the world-tile coordinates to units, but this result is centered in the world tile.
     * @param world
     * @return Unit coordinates for the center of the world-tile.
     */
    public static Coord2 worldToUnitCentered(Coord2 world) {
        return new Coord2(world.X() * 16 + 8, world.Y() * 16 + 8);
    }

    /**
     * Translate rgb values into an integer.
     * @param R 0-255
     * @param G 0-255
     * @param B 0-255
     * @return An integer as returned by BufferedImage.getRGB(x,y)
     */
    public static int makeColor(int R, int G, int B) {
        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;
        return 0xFF000000 | R | G | B;
    }
}
