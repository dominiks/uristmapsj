package org.uristmaps;

import com.esotericsoftware.minlog.Log;

import java.io.*;

/**
 * Created by dominik on 27.05.2015.
 */
public class WorldSites {

    public static void load() {
        Log.info("Sites", "Loading site information");

        // Read region_name*-world_sites_and_pops.txt
        File popFile = FileFinder.getPopulationFile();
        if (popFile == null) {
            System.exit(1);
        }

        Map<Integer, Site> sites = new HashMap<Integer, Site>();

        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {

            boolean parsingSites = false; // Is active when we have reached the correct section in the file
            String line;
            while ((line = reader.readLine()) != null) {

                // Stop reading when we reach the section about outdoor population
                // TODO: Read this for some world info.
                if (line.startsWith("Outdoor Animal Populations")) {
                    break;
                }
                if (!parsingSites && line.startsWith("Sites")) {
                    parsingSites = true;
                } else if (parsingSites) {

                }
            }
        } catch (Exception e) {
            Log.error("WorldSites", "Could not read world population file.");
            if (Log.DEBUG) Log.debug("Exception: ", e);
            System.exit(1);
        }
    }
}
