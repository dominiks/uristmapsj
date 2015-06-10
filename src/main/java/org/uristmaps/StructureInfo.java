package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.util.ExportFiles;

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
    private static Map<Integer, String> hydroColors;

    private static BufferedImage structImage;
    private static BufferedImage hydroImage;

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
        colorTranslation.put(makeColor(  0, 128,   0), "woodland");
        colorTranslation.put(makeColor(  0, 160,   0), "orchard");
        colorTranslation.put(makeColor( 20,  20,  20), "tunnel");
        // TODO: Render bridges!
        colorTranslation.put(makeColor(224, 224, 224), "stone_bridge");
        colorTranslation.put(makeColor(180, 167,  20), "other_bridge");

        colorTranslation.put(makeColor(192, 192, 192), "road");  // stone_road
        colorTranslation.put(makeColor(150, 127,  20), "road");  // other_road
        colorTranslation.put(makeColor( 96,  96,  96), "stone_wall");
        colorTranslation.put(makeColor(160, 127,  20), "other_wall");
        colorTranslation.put(makeColor(  0,  96, 255), "lake");

        hydroColors = new HashMap<>();
        hydroColors.put(makeColor(  0, 224, 255), "river");
        hydroColors.put(makeColor(  0, 255, 255), "river");
        hydroColors.put(makeColor(  0, 112, 255), "river");
    }


    /**
     * Load the structure data from the map file and save it to the build dir.
     */
    public static void load() {
        Log.debug("StructureInfo", "Loading");
        try {
            structImage = ImageIO.read(ExportFiles.getStructuresMap());
        } catch (IOException e) {
            Log.error("StructureInfo", "Could not read structure export image.");
            if (Log.DEBUG) Log.debug("StructureInfo", "Exception", e);
            System.exit(1);
        }
        try {
            hydroImage = ImageIO.read(ExportFiles.getHydroMap());
        } catch (IOException e) {
            Log.error("StructureInfo", "Could not read hydro export image.");
            if (Log.DEBUG) Log.debug("StructureInfo", "Exception", e);
            System.exit(1);
        }
        updateStructureConnections();
        Log.debug("StructureInfo", "Done");
    }

    /**
     * Iterate over all structures and check if any neighbouring tiles are of the
     * same type. When same types are found, set the suffix of the tile to reflect
     * the direction in which a same typed tile lays.
     */
    private static void updateStructureConnections() {
        if (structImage == null) load();
        structures = new String[structImage.getWidth()][structImage.getHeight()];
        StringBuilder suffix = new StringBuilder();
        String current;
        String neighbour;
        for (int x = 0; x < structImage.getWidth(); x++) {
            for (int y = 0; y < structImage.getHeight(); y++) {
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
        if (structImage == null) load();
        if (x < 0 || y < 0) return null;
        if (x >= structImage.getWidth() || y >= structImage.getHeight()) return null;

        // First check if there is a river, as that is more important!
        String result = hydroColors.get(hydroImage.getRGB(x,y));
        if (result != null) return result;
        return colorTranslation.get(structImage.getRGB(x, y));
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
    public static String getSuffixedAt(int x, int y) {
        if (structures == null) updateStructureConnections();
        return structures[x][y];
    }


    public static String[][] getSuffixed() {
        if (structures == null) updateStructureConnections();
        return structures;
    }
}
