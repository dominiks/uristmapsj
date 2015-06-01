package org.uristmaps.util;

import org.uristmaps.Uristmaps;

import java.io.File;
import java.nio.file.Paths;

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
}
