package org.uristmaps.util;

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
        File[] searchResult = new File(conf.fetch("Paths", "region")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-world_sites_and_pops.txt"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find population file in " + conf.fetch("paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    public static File getLegendsXML() {
        File[] searchResult = new File(conf.fetch("Paths", "region")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-legends.xml"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find legends xml file in " + conf.fetch("paths", "export"));
            return null;
        }
        return searchResult[0];
    }


    /**
     * Return the path to the file-state file.
     * Contains a Map<String, FileInfo> object.
     * @return
     */
    public static File getFileStore() {
        File result = new File(conf.fetch("Paths", "build"), "files.kryo");
        return result;
    }

    /**
     * Contains a WorldInfo object.
     * @return
     */
    public static File getWorldFile() {
        File result = new File(conf.fetch("Paths", "build"), "worldinfo.kryo");
        return result;
    }

    public static File getBiomeMap() {
        File[] searchResult = new File(conf.fetch("Paths", "region")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-bm.png"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find legends xml file in " + conf.fetch("paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    public static File getWorldHistory() {
        File[] searchResult = new File(conf.fetch("Paths", "region")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-world_history.txt"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find history file in " + conf.fetch("paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * The biome data file. Contains a String[][] array that maps x,y to biome name.
     * @return
     */
    public static File getBiomeInfo() {
        File result = new File(conf.fetch("Paths", "build"), "biomes.kryo");
        return result;
    }
}