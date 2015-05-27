package org.uristmaps.util;

import org.uristmaps.data.Coord2;

/**
 * Some utility stuff that gathered for the project.
 */
public class Util {

    public static final int NOT_FOUND = -1;

    /**
     * Removes the extension from a filename.
     *
     * Adapted from apache commons.io FilenameUtils.
     * @param filename The name of the file containing a file extension.
     * @return The filename without the extension and the dot at the end.
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        final int index = filename.lastIndexOf(".");
        if (index == NOT_FOUND) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

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
}
