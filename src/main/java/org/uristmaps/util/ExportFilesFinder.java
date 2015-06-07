package org.uristmaps.util;

import java.io.File;

import static org.uristmaps.Uristmaps.conf;

/**
 * Provides easy access to resource files.
 */
public class ExportFilesFinder {

    /**
     * The timestamp of the world export found in filenames.
     */
    private static String timeStamp;

    /**
     * Retrieve the population report file.
     * The site is named "`region_name`-*-world_sites_and_pops.txt".
     * @return A file reference to the file or null if no file was found.
     */
    public static File getPopulationFile() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-world_sites_and_pops.txt", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * Find the date
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
            } else {
                // Use the config as provided
                timeStamp = config;
            }
        }
        return timeStamp;
    }

    /**
     * DOCME
     * @return
     */
    public static File getLegendsXML() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-legends.xml", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * DOCME
     * @return
     */
    public static File getBiomeMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-bm.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * DOCME
     * @return
     */
    public static File getWorldHistory() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-world_history.txt", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * DOCME
     * @return
     */
    public static File getStructuresMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-str.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
    }

    /**
     * DOCME
     * @return
     */
    public static File getHydroMap() {
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-hyd.png", conf.get("Paths", "region_name"),
                        getDate()));
        return result;
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
        File result = new File(conf.fetch("Paths", "export"),
                String.format("%s-%s-site_map-%d.png", conf.get("Paths", "region_name"),
                        getDate(), id));
        return result;
    }
}
