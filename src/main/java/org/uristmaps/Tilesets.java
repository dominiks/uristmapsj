package org.uristmaps;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
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
     * TODO: Check if the source files have changed since the last compilation
     * TODO: Check if the target files are incomplete and need recompiling
     * TODO: Skip if not needed.
     */
    public static void compile() {
        Log.info("Tileset", "Compiling tileset files.");

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
        int x = -1;
        int y = -1;

        for (File imageFile : files) {
            // Increment the coordinate counters first thing, as the image might get skipped
            x++;
            if (x >= tilesPerRow) {
                x = 0;
                y++;
            }

            try {
                BufferedImage tile = ImageIO.read(imageFile);
                graphics.drawImage(tile, x * tileSize, y * tileSize, null);
                index.put(Util.removeExtension(imageFile.getName()), new Coord2(x * tileSize, y*tileSize));
            } catch (IOException e) {
                // Could not read the image
                Log.warn("Tileset", "Could not read image file: " + imageFile);
                if (Log.DEBUG) Log.debug("Exception: ", e);
                continue;
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
}