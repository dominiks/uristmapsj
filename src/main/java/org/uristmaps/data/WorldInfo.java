package org.uristmaps.data;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data container for information about the DF world that is being rendered.
 */
public class WorldInfo {

    /**
     * The cached data for the world info.
     */
    private static Map<String, String> data;

    /**
     * Load world info from the export files.
     */
    public static void load() {
        data = new HashMap<>();
        // World size will be taken from the biome export map
        loadWorldSize();
        loadNameFromHistory();

        // Export the worldfile
        File worldInfoFile = BuildFiles.getWorldFile();
        try (Output output = new Output(new FileOutputStream(worldInfoFile))) {
            Uristmaps.kryo.writeObject(output, data);
        } catch (FileNotFoundException e) {
            Log.warn("WorldInfo", "Error when writing state file: " + worldInfoFile);
            if (Log.DEBUG) Log.debug("WorldInfo", "Exception", e);
        }
    }

    /**
     * Load the name(s) from the history file.
     */
    private static void loadNameFromHistory() {
        Log.debug("WorldInfo", "Reading population counts.");
        try (BufferedReader reader = new BufferedReader(new FileReader(ExportFiles.getWorldHistory()))) {
            data.put("name", reader.readLine());
            data.put("nameEnglish", reader.readLine());
        } catch (Exception e) {
            Log.error("WorldInfo", "Could not read population info file.");
            if (Log.DEBUG) Log.debug("WorldInfo", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Load the biome map and check its px size for world unit-size.
     */
    private static void loadWorldSize() {
        Log.debug("WorldInfo", "Importing world size from biome");

        try {
            BufferedImage image = ImageIO.read(ExportFiles.getBiomeMap());
            data.put("size", image.getWidth() + "");
        } catch (IOException e) {
            Log.error("WorldInfo", "Could not read biome image file.");
            if (Log.DEBUG) Log.debug("WorldInfo", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Get the dimension size of the world (width or height).
     * @return
     */
    public static int getSize() {
        if (data == null) loadData();
        return Integer.parseInt(data.get("size"));
    }

    /**
     * Load the saved data.
     */
    private static void loadData() {
        try (Input input = new Input(new FileInputStream(BuildFiles.getWorldFile()))) {
            data = Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (Exception e) {
            Log.error("WorldInfo", "Could not read world info file: " + BuildFiles.getWorldFile());
            if (Log.DEBUG) Log.debug("WorldInfo", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Get the dwarven name of this world.
     * @return
     */
    public static String getName() {
        if (data == null) loadData();
        return data.get("name");
    }

    /**
     * Get the english translation of the world's name.
     * @return
     */
    public static String getNameEnglish() {
        if (data == null) loadData();
        return data.get("nameEnglish");
    }

    /**
     * Return a direct reference to the data object. Used for velocity.
     * @return
     */
    public static Map<String, String> getData() {
        if (data == null) loadData();
        return data;
    }
}
