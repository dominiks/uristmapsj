package org.uristmaps;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFilesFinder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
        } catch (IOException e) {
            Log.error("StructureInfo", "Could not read structure export image.");
            if (Log.DEBUG) Log.debug("StructureInfo", "Exception", e);
            System.exit(1);
        }
        Log.info("StructureInfo", "Done");
    }

    /**
     * Get the data.
     * @return
     */
    public static String getData(int x, int y) {
        if (image == null) load();
        return colorTranslation.get(image.getRGB(x, y));
    }

}
