package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FilenameUtils;
import org.uristmaps.data.Coord2;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dominik on 26.05.2015.
 */
public class Tilesets {

    /**
     * Iterate over the PNG files within the specified directory and
     * create a tileset image and the index file for this.
     * @param directory The directory to walk through.
     */
    public static void compileDirectory(File directory) {
        File[] files = directory.listFiles(pathname -> pathname.getName().endsWith(".png"));
        Log.debug("Tileset", "Found " + files.length + " images.");
        Log.info("Tileset", "Compiling tileset in " + directory);

        int tileSize = 0;
        try {
            tileSize = Integer.parseInt(directory.getName());
        } catch (NumberFormatException e) {
            Log.warn("Tileset", "Could not parse size from directory: " + directory.getName());
            return;
        }

        Map<String, Coord2> index = new HashMap<>();

        // Size the image to be a square that can contain all tiles.
        int tilesPerRow = (int) Math.ceil(Math.sqrt(files.length));
        BufferedImage result = new BufferedImage(tilesPerRow * tileSize, tilesPerRow * tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        int x = 0;
        int y = 0;

        for (File imageFile : files) {
            try {
                BufferedImage tile = ImageIO.read(imageFile);
                graphics.drawImage(tile, x * tileSize, y * tileSize, null);
                index.put(FilenameUtils.removeExtension(imageFile.getName()), new Coord2(x * tileSize, y*tileSize));
            } catch (IOException e) {
                // Could not read the image
                Log.warn("Tileset", "Could not read image file: " + imageFile);
                if (Log.DEBUG) Log.debug("Exception: ", e);
                continue;
            } finally {
                x++;
                if (x >= tilesPerRow) {
                    x = 0;
                    y++;
                }
            }
        }

        // Save state for all files
        Uristmaps.files.updateFiles(files);

        // Store the tileset image
        try {
            ImageIO.write(result, "PNG", BuildFiles.getTilesetImage(tileSize));
        } catch (Exception e) {
            Log.warn("Tilesets", "Could not write tileset image file: " + BuildFiles.getTilesetImage(tileSize));
            if (Log.DEBUG) Log.debug("Exception: ", e);
        }

        // Export the index file
        try (Output output = new Output(new FileOutputStream(BuildFiles.getTilesetIndex(tileSize)))) {
            Uristmaps.kryo.writeObject(output, index);
        } catch (Exception e) {
            Log.warn("Tilesets", "Could not write tileset index file: " + BuildFiles.getTilesetIndex(tileSize));
            if (Log.DEBUG) Log.debug("Exception: ", e);
        }
    }

    /**
     * Load the image data for the tileset of the given level.
     * @param level
     * @return
     */
    public static BufferedImage getTilesetImage(int level) {
        try {
            return ImageIO.read(BuildFiles.getTilesetImage(level));
        } catch (IOException e) {
            Log.error("Tilesets", "Could not read tileset image file: " + BuildFiles.getTilesetImage(level));
            if (Log.DEBUG) Log.debug("Tilesets", "Exception", e);
            System.exit(1);
        }
        return null;
    }

    /**
     * Load the index data for the tileset of the given level.
     * @param level
     * @return
     */
    public static Map<String, Coord2> getTilesetIndex(int level) {
        try (Input input = new Input(new FileInputStream(BuildFiles.getTilesetIndex(level)))) {
            return Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (Exception e) {
            Log.error("Tilesets", "Could not read tileset index file: " + BuildFiles.getTilesetIndex(level));
            if (Log.DEBUG) Log.debug("Tilesets", "Exception: ", e);
        }
        return null;
    }
}
