package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.util.FileFinder;

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

    public static String[][] biomeInfo;


    public static void load() {
        Log.info("BiomeInfo", "Loading biome data.");
        // Check if source file has changed or target is missing
        if (!Uristmaps.files.fileOk(FileFinder.getBiomeMap())
                || !FileFinder.getBiomeInfo().exists()) {
            reload();
        } else {
            File biomeInfoFile = FileFinder.getBiomeInfo();
            try (Input input = new Input(new FileInputStream(biomeInfoFile))) {
                String[][] biomes = Uristmaps.kryo.readObject(input, String[][].class);
                biomeInfo = biomes;
            } catch (FileNotFoundException e) {
                Log.warn("BiomeInfo", "Error when reading biome file: " + biomeInfoFile);
                if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
            }
        }
    }

    /**
     * Reload the biome information from the biome export map and write to biomes.kryo.
     */
    private static void reload() {
        // Open biome image
        Log.info("BiomeInfo", "Reloading biome data from exported image.");
        BufferedImage image = null;
        try {
            image = ImageIO.read(FileFinder.getBiomeMap());
        } catch (IOException e) {
            Log.error("BiomeInfo", "Could not read biome export image.");
            if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
            System.exit(1);
        }

        String[][] result = new String[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                result[x][y] = colorTranslation.get(image.getRGB(x,y));
            }
        }

        Log.info("BiomeInfo", "Done");
        biomeInfo = result;
        File biomeInfoFile = FileFinder.getBiomeInfo();
        try (Output output = new Output(new FileOutputStream(biomeInfoFile))) {
            Uristmaps.kryo.writeObject(output, result);
        } catch (FileNotFoundException e) {
            Log.warn("BiomeInfo", "Error when writing biome file: " + biomeInfoFile);
            if (Log.DEBUG) Log.debug("BiomeInfo", "Exception", e);
        }
    }

    /**
     * Translate rgb values into an integer.
     * @param R 0-255
     * @param G 0-255
     * @param B 0-255
     * @return An integer as returned by BufferedImage.getRGB(x,y)
     */
    private static int makeColor(int R, int G, int B) {
        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;
        return 0xFF000000 | R | G | B;
    }
}
