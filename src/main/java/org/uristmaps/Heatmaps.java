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

    /**
     * DOCME
     */
    static Map<String, String> nameTransform = new HashMap<>();

    static {
        nameTransform.put("Bat", "Bats");
        nameTransform.put("Bleak Man", "Bleak Men");
        nameTransform.put("Blind Horror", "Blind Horrors");
        nameTransform.put("Bronze Colossus", "Bronze Colossuses");
        nameTransform.put("Cat", "Cats");
        nameTransform.put("Cougar", "Cougars");
        nameTransform.put("Creature Of Twilight", "Creatures Of Twilight");
        nameTransform.put("Cyclops", "Cyclopses");
        nameTransform.put("Dark Creature", "Dark Creatures");
        nameTransform.put("Dingo", "Dingoes");
        nameTransform.put("Dingo Man", "Dingo Men");
        nameTransform.put("Dragon", "Dragons");
        nameTransform.put("Dusk Horror", "Dusk Horrors");
        nameTransform.put("Dwarf", "Dwarves");
        nameTransform.put("Elf", "Elves");
        nameTransform.put("Ettin", "Ettins");
        nameTransform.put("Forest Titan", "Forest Titans");
        nameTransform.put("Giant", "Giants");
        nameTransform.put("Giant Dingo", "Giant Dingoes");
        nameTransform.put("Giant Jaguar", "Giant Jaguars");
        nameTransform.put("Giant Leopard", "Giant Leopards");
        nameTransform.put("Giant Tiger", "Giant Tigers");
        nameTransform.put("Goblin", "Goblins");
        nameTransform.put("Goblin Outcast", "Goblin Outcasts");
        nameTransform.put("Grizzly Bear", "Grizzly Bears");
        nameTransform.put("Hill Titan", "Hill Titans");
        nameTransform.put("Human", "Humans");
        nameTransform.put("Hydra", "Hydras");
        nameTransform.put("Hyena Man", "Hyena Men");
        nameTransform.put("Jaguar", "Jaguars");
        nameTransform.put("Jungle Titan", "Jungle Titans");
        nameTransform.put("Kobold", "Kobolds");
        nameTransform.put("Marsh Titan", "Marsh Titans");
        nameTransform.put("Midnight Brute", "Midnight Brutes");
        nameTransform.put("Minotaur", "Minotaurs");
        nameTransform.put("Monster Of Twilight", "Monsters Of Twilight");
        nameTransform.put("Plains Titan", "Plains Titans");
        nameTransform.put("Polar Bear", "Polar Bears");
        nameTransform.put("Roc", "Rocs");
        nameTransform.put("Sasquatch", "Sasquatches");
        nameTransform.put("Tiger Man", "Tiger Men");
        nameTransform.put("Wicked Freak", "Wicked Freaks");
        nameTransform.put("Tundra Titan", "Tundra Titans");
        nameTransform.put("Wicked Creature", "Wicked Creatures");
    }

    public static void writePopInfo() {
        Log.info("Heatmaps", "Writing population info");

        // Load site info
        Map<Integer, Site> sites = WorldSites.getSites();

        // Create a map, mapping races to a list of sites where they can be found
        TreeMap<String, List<Site>> popDistribution = new TreeMap<>();

        // Map the race name to the single biggest population count in a single site
        Map<String, Integer> maxPop = new HashMap<>();
        for (Site site : sites.values()) {

            String raceName;
            for (Map.Entry<String, Integer> entry : site.getPopulations().entrySet()) {
                if (nameTransform.containsKey(entry.getKey())) {
                    raceName = nameTransform.get(entry.getKey());
                } else {
                    System.err.println(entry.getKey());
                    raceName = entry.getKey();
                }

                // Update maximum population for that race
                if (!maxPop.containsKey(raceName)) {
                    maxPop.put(raceName, entry.getValue());
                } else if (maxPop.get(raceName) < entry.getValue()) {
                    maxPop.put(raceName, entry.getValue());
                }

                if (!popDistribution.containsKey(raceName)) {
                    popDistribution.put(raceName, new LinkedList<>());
                }
                popDistribution.get(raceName).add(site);
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
            fileContent.append("\"").append(raceName).append("\":{");
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
