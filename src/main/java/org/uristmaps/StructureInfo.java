package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.util.ExportFilesFinder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.uristmaps.util.Util.makeColor;


/**
 * Processes structure position data from the str-map.
 */
public class StructureInfo {

    private static Map<Integer, String> colorTranslation;
    private static BufferedImage image;
    private static String[][] structures;

    static {
        colorTranslation = new HashMap<>();
        colorTranslation.put(makeColor(128, 128, 128), "castle");
        colorTranslation.put(makeColor(255, 255, 255), "village");
        colorTranslation.put(makeColor(255, 128,   0), "crops");
        colorTranslation.put(makeColor(255, 160,   0), "crops");
        colorTranslation.put(makeColor(255, 192,   0), "crops");
        colorTranslation.put(makeColor( 0,  255,   0), "pasture");
        colorTranslation.put(makeColor( 64, 255,   0), "meadow");
        colorTranslation.put(makeColor(  0, 160,   0), "orchard");
        colorTranslation.put(makeColor( 20,  20,  20), "tunnel");
        colorTranslation.put(makeColor(224, 224, 224), "stone_bridge");
        colorTranslation.put(makeColor(180, 167,  20), "other_bridge");
        colorTranslation.put(makeColor(192, 192, 192), "road");  // stone_road
        colorTranslation.put(makeColor(150, 127,  20), "road");  // other_road
        colorTranslation.put(makeColor( 96,  96,  96), "stone_wall");
        colorTranslation.put(makeColor(160, 127,  20), "other_wall");
        colorTranslation.put(makeColor(  0,  96, 255), "lake");
    }

    /**
     * Load the structure data from the map file and save it to the build dir.
     */
    public static void load() {
        try {
            image = ImageIO.read(ExportFilesFinder.getBiomeMap());
            updateStructureConnections();
        } catch (IOException e) {
            Log.error("StructureInfo", "Could not read structure export image.");
            if (Log.DEBUG) Log.debug("StructureInfo", "Exception", e);
            System.exit(1);
        }
        Log.info("StructureInfo", "Done");
    }

    /**
     * Iterate over all structures and check if any neighbouring tiles are of the
     * same type. When same types are found, set the suffix of the tile to reflect
     * the direction in which a same typed tile lays.
     */
    private static void updateStructureConnections() {
        if (image == null) load();
        structures = new String[image.getWidth()][image.getHeight()];
        StringBuilder suffix = new StringBuilder();
        String current;
        String neighbour;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // Reset the suffix builder, set current structure
                current = getData(x,y);

                // Skip if nothing here
                if (current == null) continue;

                // Reset the suffix-builder
                suffix.setLength(0);
                suffix.append(current).append("_");

                neighbour = getData(x, y-1);
                if (neighbour != null && neighbour.equals(current)) {
                    suffix.append("n");
                }
                neighbour = getData(x-1, y);
                if (neighbour != null && neighbour.equals(current)) {
                    suffix.append("w");
                }
                neighbour = getData(x, y+1);
                if (neighbour != null && neighbour.equals(current)) {
                    suffix.append("s");
                }
                neighbour = getData(x+1, y);
                if (neighbour != null && neighbour.equals(current)) {
                    suffix.append("e");
                }

                // Remove the _ at the end if no suffixes were added.
                if (suffix.charAt(suffix.length()-1) == '_') suffix.deleteCharAt(suffix.length()-1);
                structures[x][y] = suffix.toString();
            }
        }

    }

    /**
     * Get type value for given coordinates.
     * @param x X coordinate
     * @param y Y coordinate
     * @return Null when nothing could be found.
     */
    public static String getData(int x, int y) {
        if (image == null) load();
        if (x < 0 || y < 0) return null;
        if (x >= image.getWidth() || y >= image.getHeight()) return null;
        return colorTranslation.get(image.getRGB(x, y));
    }

    /**
     * Get the prefixed structure name for the given tile.
     * The suffix might contain any of these letters: nwse
     * in that order but not necessarily all of them.
     *
     * For example "cave_ne" for a cave that has another caves in north
     * and east.
     * @param x
     * @param y
     * @return The suffixed structure type or null if nothing was found.
     */
    public static String getSuffixed(int x, int y) {
        if (structures == null) updateStructureConnections();
        return structures[x][y];
    }

}
