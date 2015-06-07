package org.uristmaps.util;

import com.esotericsoftware.minlog.Log;

import java.io.File;

import static org.uristmaps.Uristmaps.conf;

/**
 * Provides easy access to resource files.
 */
public class ExportFiles {

    /**
     * The timestamp of the world export found in filenames.
     */
    private static String timeStamp;

    /**
     * Retrieve the population report file.
     * The site is named "`region_name`-*-world_sites_and_pops.txt".
     * Does not check if the file exists!
     * @return A file reference to the file
     */
    public static File getPopulationFile() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-world_sites_and_pops.txt", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Find the date of the export files. Either this is set in the config or the latest date
     * is resolved using the legends.xml file with the latest date.
     * @return
     */
    public static String getDate() {
        if (timeStamp == null) {
            String config = conf.get("Paths", "region_date");
            if (config.equals("@LATEST")) {
                // Find all *-legends.xml files
                File[] populationFiles = new File(conf.fetch("Paths", "export")).listFiles(
                        (dir, name) -> name.startsWith(conf.get("Paths", "region_name"))
                                && name.endsWith("-legends.xml"));

                // Find the maximum date string within these filenames
                String maxDate = "00000-00-00";
                for (File popFile : populationFiles) {
                    String fileName = popFile.getName();
                    String date = fileName.replace(conf.get("Paths", "region_name") + "-", "").replace("-legends.xml", "");
                    if (maxDate.compareTo(date) < 0) maxDate = date;
                }
                timeStamp = maxDate;
                Log.info("ExportFiles", "Resolved date to " + maxDate);
            } else {
                // Use the config as provided
                timeStamp = config;
            }
        }
        return timeStamp;
    }

    /**
     * Resolve the path to the legends xml file.
     * @return File reference to where this file should be.
     */
    public static File getLegendsXML() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-legends.xml", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Resolve the path to the biome map image.
     * Does not check if the file exists!
     * @return File reference to where this file should be.
     */
    public static File getBiomeMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-bm.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Construct the path to the world_history.txt file.
     * Does not check if the file exists!
     * @return
     */
    public static File getWorldHistory() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-world_history.txt", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Construct the path to the structures map image file.
     * @return File reference to where this file should be.
     */
    public static File getStructuresMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-str.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Construct the path to the hydro map image file.
     * @return File reference to where this file should be.
     */
    public static File getHydroMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-hyd.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Find all detailed site maps for the current region & date.
     * @return File reference to where this file should be.
     */
    public static File[] getAllSitemaps() {
        return new File(conf.fetch("Paths", "export")).listFiles((dir, name) -> name.startsWith(
                    String.format("%s-%s.site_map-", conf.get("Paths", "region_name"), getDate()))
                && name.endsWith(".png")
        );
    }

    /**
     * Construct the path to the detailed site map for a given site.
     * Does not check if the file exists!
     * @param id The id of the site.
     * @return
     */
    public static File getSiteMap(int id) {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-site_map-%d.png", conf.get("Paths", "region_name"),
                        getDate(), id));
        return result;
    }
}
