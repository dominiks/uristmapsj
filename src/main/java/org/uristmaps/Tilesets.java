package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FilenameUtils;
import org.uristmaps.data.Coord2;
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
     * Check the tile directory for new loose tiles and compile
     * them into the tileset file.
     *
     */
    public static void compile() {

        // Iterate over directories in specified tileset dir
        File tilesDir = new File(Uristmaps.conf.fetch("Paths", "tiles")).getAbsoluteFile();

        if (!tilesDir.exists()) {
            Log.error("Tileset", "Could not find tiles directory: " + tilesDir);
            System.exit(1);
        }

        for (File tileDir : tilesDir.listFiles(fname -> fname.isDirectory())) {
            Log.debug("Tileset", "Processing directory: " + tileDir);
            compileDirectory(tileDir);
        }
    }

    /**
     * Iterate over the PNG files within the specified directory and
     * create a tileset image and the index file for this.
     * @param directory The directory to walk through.
     */
    private static void compileDirectory(File directory) {
        File[] files = directory.listFiles(pathname -> pathname.getName().endsWith(".png"));
        Log.debug("Tileset", "Found " + files.length + " images.");

        // Check if any files need refreshing.
        // If we did .dirty to get only the dirty files and recompiled them, the result would be a
        // tilesheet of only these tiles.
        boolean needRefresh = !Uristmaps.files.allOk(files);
        if (!needRefresh) return;

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
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "tilesets"),
                Integer.toString(tileSize) + ".png").toFile();
        try {
            ImageIO.write(result, "PNG", targetFile);
        } catch (Exception e) {
            Log.warn("Tilesets", "Could not write tileset image file: " + targetFile);
            if (Log.DEBUG) Log.debug("Exception: ", e);
        }

        // TODO: Export the index file
        targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "tilesets"),
                Integer.toString(tileSize) + ".kryo").toFile();
        try (Output output = new Output(new FileOutputStream(targetFile))) {
            Uristmaps.kryo.writeObject(output, index);
        } catch (Exception e) {
            Log.warn("Tilesets", "Could not write tileset index file: " + targetFile);
            if (Log.DEBUG) Log.debug("Exception: ", e);
        }
    }

    /**
     * Load the image data for the tileset of the given level.
     * @param level
     * @return
     */
    public static BufferedImage getTilesetImage(int level) {
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "tilesets"),
                Integer.toString(level) + ".png").toFile();
        try {
            return ImageIO.read(targetFile);
        } catch (IOException e) {
            Log.error("Tilesets", "Could not read tileset image file: " + targetFile);
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
        Map<String, Coord2> result = null;
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "tilesets"),
                Integer.toString(level) + ".kryo").toFile();
        try (Input input = new Input(new FileInputStream(targetFile))) {
            result = Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (Exception e) {
            Log.warn("Tilesets", "Could not read tileset index file: " + targetFile);
            if (Log.DEBUG) Log.debug("Tilesets", "Exception: ", e);
        }
        return result;
    }
}
