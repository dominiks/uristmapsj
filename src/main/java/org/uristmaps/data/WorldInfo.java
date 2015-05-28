package org.uristmaps.data;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;
import org.uristmaps.util.FileFinder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Data container for information about the DF world that is being rendered.
 */
public class WorldInfo {

    private int size;
    private String name;
    private String nameEnglish;

    public WorldInfo() {

    }

    /**
     * Load world info from the export files.
     */
    public void init() {
        // World size will be taken from the biome export map
        boolean readBiome = loadWorldSize();
        boolean readHistory = loadNameFromHistory();

        if (readBiome || readHistory) {
            // Export the worldfile
            File worldInfoFile = FileFinder.getWorldFile();
            try (Output output = new Output(new FileOutputStream(worldInfoFile))) {
                Uristmaps.kryo.writeObject(output, this);
            } catch (FileNotFoundException e) {
                Log.warn("FileWatcher", "Error when writing state file: " + worldInfoFile);
                if (Log.DEBUG) Log.debug("FileWatcher", "Exception", e);
            }
            Log.debug("FileWatcher", "Saved store.");
        }
    }

    private boolean loadNameFromHistory() {
        if (Uristmaps.files.fileOk(FileFinder.getWorldHistory())) {
            Log.debug("WorldInfo", "Skipping world history file.");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FileFinder.getWorldHistory()))) {
            name = reader.readLine();
            nameEnglish = reader.readLine();
        } catch (Exception e) {
            Log.error("WorldInfo", "Could not read world history file.");
            if (Log.DEBUG) Log.debug("WorldInfo", "Exception", e);
        }
        Uristmaps.files.updateFile(FileFinder.getWorldHistory());
        return true;
    }

    /**
     * Load the biome map and check its px size for world unit-size.
     */
    private boolean loadWorldSize() {
        if (Uristmaps.files.fileOk(FileFinder.getBiomeMap()) && FileFinder.getWorldFile().exists()) {
            Log.debug("WorldInfo", "Skipping biome map");
            return false;
        }
        Log.debug("WorldInfo", "Importing world size from biome");

        try {
            BufferedImage image = ImageIO.read(FileFinder.getBiomeMap());
            size = image.getWidth();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uristmaps.files.updateFile(FileFinder.getBiomeMap());
        return true;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

}
