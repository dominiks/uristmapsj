package org.uristmaps.util;

import org.uristmaps.Uristmaps;

import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides easy access to files in the output directory.
 */
public class OutputFiles {

    /**
     * Return the path to the sites geoJson file in the output.
     * @return
     */
    public static File getSitesGeojson() {
        return Paths.get(Uristmaps.conf.fetch("Paths", "output"), "js", "sitesgeo.json").toFile();
    }

    public static File getUristJs() {
        return Paths.get(Uristmaps.conf.fetch("Paths", "output"), "js", "urist.js").toFile();
    }

    public static File getIndexHtml() {
        return Paths.get(Uristmaps.conf.fetch("Paths", "output"), "urist.js").toFile();
    }

    /**
     * Return pathes for all tile files for a given level.
     * @param tiles The folder where the tile images will be placed.
     * @param level The zoom level for the images.
     * @return
     */
    public static File[] getLayerImages(String tiles, int level) {
        int dimension = (int) Math.pow(2, level);
        List<File> result = new LinkedList<>();
        for (int x = 0; x < dimension; x++) {
            for (int y = 0; y < dimension; y++) {
                result.add(Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                        tiles, Integer.toString(level), Integer.toString(x), y + ".png").toFile());
            }
        }
        return result.toArray(new File[0]);
    }
}
