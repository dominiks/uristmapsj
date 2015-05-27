package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Site;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dominik on 27.05.2015.
 */
public class WorldSites {

    private static final Pattern idReader = Pattern.compile("(\\d+):");

    public static void load() {
        Log.info("Sites", "Loading site information");

        // Read region_name*-world_sites_and_pops.txt
        File popFile = FileFinder.getPopulationFile();
        if (popFile == null) {
            System.exit(1);
        }

        // The result of all collected site info
        Map<Integer, Site> sites = new HashMap<Integer, Site>();

        // The last read site
        Site lastSite = null;

        // The matcher object for the regex
        Matcher match = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {

            boolean parsingSites = false; // Is active when we have reached the correct section in the file
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove whitespace

                // Stop reading when we reach the section about outdoor population
                // TODO: Read this for some world info.
                if (line.startsWith("Outdoor Animal Populations")) {
                    break;
                }
                if (!parsingSites && line.startsWith("Sites")) {
                    parsingSites = true;
                } else if (parsingSites) {
                    match = idReader.matcher(line);
                    // See if this is the first line of a new site
                    if (match.matches()) {
                        lastSite = new Site(Integer.parseInt(match.group(0)));
                        lastSite.addInfo(line);

                    // Parse this as info about the last created site
                    } else if (lastSite != null) {
                        lastSite.addInfo(line);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("WorldSites", "Could not read world population file.");
            if (Log.DEBUG) Log.debug("Exception: ", e);
            System.exit(1);
        }
    }
}
