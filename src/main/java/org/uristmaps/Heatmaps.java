package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.Coord2d;
import org.uristmaps.data.Site;
import org.uristmaps.util.OutputFiles;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by dominik on 09.06.2015.
 */
public class Heatmaps {

    public static void writePopInfo() {
        Log.info("Heatmaps", "Writing population info");

        // Load site info
        Map<Integer, Site> sites = WorldSites.getSites();

        // Create a map, mapping races to a list of sites where they can be found
        TreeMap<String, List<Site>> popDistribution = new TreeMap<>();

        // Map the race name to the single biggest population count in a single site
        Map<String, Integer> maxPop = new HashMap<>();
        for (Site site : sites.values()) {
            for (Map.Entry<String, Integer> entry : site.getPopulations().entrySet()) {

                // Update maximum population for that race
                if (!maxPop.containsKey(entry.getKey())) {
                    maxPop.put(entry.getKey(), entry.getValue());
                } else if (maxPop.get(entry.getKey()) < entry.getValue()) {
                    maxPop.put(entry.getKey(), entry.getValue());
                }

                if (!popDistribution.containsKey(entry.getKey())) {
                    popDistribution.put(entry.getKey(), new LinkedList<>());
                }
                popDistribution.get(entry.getKey()).add(site);
            }
        }

        for (Map.Entry<Integer, Coord2> entry : SiteCenters.getCenters().entrySet()) {
            Site site = WorldSites.getSites().get(entry.getKey());
            site.setCoords(entry.getValue());
            Coord2d latlon = WorldSites.xy2LonLat(site.getCoords().X(), site.getCoords().Y());
            site.setLat(latlon.X());
            site.setLon(latlon.Y());
        }

        // Write the js data
        StringBuilder fileContent = new StringBuilder("var populations = {");
        for (String raceName : popDistribution.navigableKeySet()) {
            fileContent.append(raceName.trim().replace(" ", "_")).append(":{");
            fileContent.append("max:").append(maxPop.get(raceName)).append(",data:[");
            for (Site site : popDistribution.get(raceName)) {
                fileContent.append("{lat:").append(site.getLat());
                fileContent.append(",lng:").append(site.getLon());
                fileContent.append(",count:").append(site.getPopulations().get(raceName));
                fileContent.append("},");
            }
            fileContent.deleteCharAt(fileContent.length() - 1); // Remove last ,
            fileContent.append("]},\n");
        }
        fileContent.append("};");

        try (FileWriter writer = new FileWriter(OutputFiles.getPopulationJs())) {
            writer.write(fileContent.toString());
        } catch (IOException e) {
            Log.error("Heatmaps", "Could not write population file: " + OutputFiles.getPopulationJs());
            if (Log.DEBUG) Log.debug("Heatmaps", "Exception", e);
            System.exit(1);
        }
    }
}
