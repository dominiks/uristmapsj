package org.uristmaps;

import com.esotericsoftware.minlog.Log;

import java.io.File;

import static org.uristmaps.Uristmaps.conf;

/**
 * Provides easy access to resource files.
 */
public class FileFinder {

    /**
     * Retrieve the population report file.
     * The site is named "`region_name`-*-world_sites_and_pops.txt".
     * @return A file reference to the file or null if no file was found.
     */
    public static File getPopulationFile() {
        File exportDir = new File(conf.fetch("Paths", "region"));
        File[] searchResult = exportDir.listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-world_sites_and_pops.txt"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find population file in " + conf.fetch("paths", "export"));
            return null;
        }
        return searchResult[0];
    }
}
