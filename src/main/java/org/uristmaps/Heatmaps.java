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
 * DOCME
 */
public class Heatmaps {



    public static void writePopulationsJS() {
        Log.info("Heatmaps", "Writing population info");

        // Write the js data
        StringBuilder fileContent = new StringBuilder("var populations = {");
        for (String raceName : WorldSites.getPopulationDistribution().navigableKeySet()) {
            fileContent.append("\"").append(raceName).append("\":{");
            fileContent.append("max:").append(WorldSites.getPopulationCounts().get(raceName)).append(",data:[");
            for (Site site : WorldSites.getPopulationDistribution().get(raceName)) {
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
