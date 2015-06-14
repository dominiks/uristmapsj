package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;

import static org.uristmaps.util.Util.makeColor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Loads the biome information for rendering.
 */
public class BiomeInfo {

    private static Map<Integer, String> colorTranslation;

    /**
     * The loaded biome data.
     */
    private static String[][] biomeData;

    static {
        colorTranslation = new HashMap<>();
        colorTranslation.put(makeColor(128,128,128), "mountain");
        colorTranslation.put(makeColor(0, 224, 255), "temperate_freshwater_lake");
        colorTranslation.put(makeColor(0, 192, 255), "temperate_brackish_lake");
        colorTranslation.put(makeColor(0,160,255), "temperate_saltwater_lake");
        colorTranslation.put(makeColor(0,96,255), "tropical_freshwater_lake");
        colorTranslation.put(makeColor(0,64,255), "tropical_brackish_lake");
        colorTranslation.put(makeColor(0,32,255), "tropical_saltwater_lake");
        colorTranslation.put(makeColor(0,255,255), "arctic_ocean");
        colorTranslation.put(makeColor(0,0,255), "tropical_ocean");
        colorTranslation.put(makeColor(0,128,255), "temperate_ocean");
        colorTranslation.put(makeColor(64,255,255), "glacier");
        colorTranslation.put(makeColor(128,255,255), "tundra");
        colorTranslation.put(makeColor(96,192,128), "temperate_freshwater_swamp");
        colorTranslation.put(makeColor(64,192,128), "temperate_saltwater_swamp");
        colorTranslation.put(makeColor(96,255,128), "temperate_freshwater_marsh");
        colorTranslation.put(makeColor(64,255,128), "temperate_saltwater_marsh");
        colorTranslation.put(makeColor(96,192,64), "tropical_freshwater_swamp");
        colorTranslation.put(makeColor(64,192,64), "tropical_saltwater_swamp");
        colorTranslation.put(makeColor(64,255,96), "mangrove_swamp");
        colorTranslation.put(makeColor(96,255,64), "tropical_freshwater_marsh");
        colorTranslation.put(makeColor(64,255,64), "tropical_saltwater_marsh");
        colorTranslation.put(makeColor(0,96,64), "taiga_forest");
        colorTranslation.put(makeColor(0,96,32), "temperate_conifer_forest");
        colorTranslation.put(makeColor(0,160,32), "temperate_broadleaf_forest");
        colorTranslation.put(makeColor(0,96,0), "tropical_conifer_forest");
        colorTranslation.put(makeColor(0,128,0), "tropical_dry_broadleaf_forest");
        colorTranslation.put(makeColor(0,160,0), "tropical_moist_broadleaf_forest");
        colorTranslation.put(makeColor(0,255,32), "temperate_grassland");
        colorTranslation.put(makeColor(0,224,32), "temperate_savanna");
        colorTranslation.put(makeColor(0,192,32), "temperate_shrubland");
        colorTranslation.put(makeColor(255,160,0), "tropical_grassland");
        colorTranslation.put(makeColor(255,176,0), "tropical_savanna");
        colorTranslation.put(makeColor(255,192,0), "tropical_shrubland");
        colorTranslation.put(makeColor(255,96,32), "badland_desert");
        colorTranslation.put(makeColor(255,255,0), "sand_desert");
        colorTranslation.put(makeColor(255,128,64), "rock_desert");
    }

    public static void load() {
        // Open biome image
        Log.info("BiomeInfo", "Reloading biome data from exported image.");
        BufferedImage image = null;
        try {
            image = ImageIO.read(ExportFiles.getBiomeMap());
        } catch (IOException e) {
            Log.error("BiomeInfo", "Could not read biome export image.");
            if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
            System.exit(1);
        }

        biomeData = new String[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                biomeData[x][y] = colorTranslation.get(image.getRGB(x, y));
            }
        }


        File biomeInfoFile = BuildFiles.getBiomeInfo();
        try (Output output = new Output(new FileOutputStream(biomeInfoFile))) {
            Uristmaps.kryo.writeObject(output, biomeData);
        } catch (FileNotFoundException e) {
            Log.warn("BiomeInfo", "Error when writing biome file: " + biomeInfoFile);
            if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
        }

        Log.info("BiomeInfo", "Done");
    }

    /**
     * Call this to retrieve the biome data.
     * This info is cached after the first call.
     * @return
     */
    public static String[][] getBiomeData() {
        if (biomeData != null) {
            return biomeData;
        }
        // TODO: Reading from the image might be faster than this kryo import.
        File biomeInfoFile = BuildFiles.getBiomeInfo();
        try (Input input = new Input(new FileInputStream(biomeInfoFile))) {
            biomeData = Uristmaps.kryo.readObject(input, String[][].class);
            return biomeData;
        } catch (FileNotFoundException e) {
            Log.warn("BiomeInfo", "Error when reading biome file: " + biomeInfoFile);
            if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
            System.exit(1);
        }
        return null;
    }
}
