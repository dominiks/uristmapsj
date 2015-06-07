package org.uristmaps.util;

import com.esotericsoftware.minlog.Log;

import java.io.File;

import static org.uristmaps.Uristmaps.conf;

/**
 * Provides easy access to resource files.
 */
public class ExportFilesFinder {

    /**
     * Retrieve the population report file.
     * The site is named "`region_name`-*-world_sites_and_pops.txt".
     * @return A file reference to the file or null if no file was found.
     */
    public static File getPopulationFile() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-world_sites_and_pops.txt"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find population file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File getLegendsXML() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-legends.xml"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find legends xml file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File getBiomeMap() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-bm.png"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find biome map file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File getWorldHistory() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-world_history.txt"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find history file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File getStructuresMap() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-str.png"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find structures map file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File getHydroMap() {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("-hyd.png"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find structures map file in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }

    /**
     * DOCME
     * @return
     */
    public static File[] getAllSitemaps() {
        return new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().contains("site_map") && filename.getName().endsWith(".png"));
    }

    /**
     * DOCME
     * @param id
     * @return
     */
    public static File getSiteMap(int id) {
        File[] searchResult = new File(conf.fetch("Paths", "export")).listFiles(
                filename -> filename.getName().contains(conf.get("Paths", "region_name"))
                        && filename.getName().endsWith("site_map-" + id + ".png"));
        if (searchResult.length == 0) {
            Log.error("Filefinder", "Could not find structures map file for site " + id + " in " + conf.fetch("Paths", "export"));
            return null;
        }
        return searchResult[0];
    }
}
