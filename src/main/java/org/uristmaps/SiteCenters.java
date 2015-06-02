package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.Site;
import org.uristmaps.data.StructureGroup;
import org.uristmaps.util.BuildFiles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 */
public class SiteCenters {

    private static final int MAX_SEARCH_RADIUS = 8;
    /**
     * Maps site types to structure objects that are associated with them.
     */
    private static Map<String, String> typeToStruct;
    private static HashMap<Integer, Coord2> siteCenters;

    static {
        typeToStruct = new HashMap<>();
        typeToStruct.put("hamlet", "village");
        typeToStruct.put("dark fortress", "castle");
        typeToStruct.put("dark pits", "castle");
        typeToStruct.put("tomb", "castle");
        typeToStruct.put("hillocks", "village");
        typeToStruct.put("town", "village");
        typeToStruct.put("forest retreat", "village");
    }

    /**
     * Update the site coordinates by centering them on the respective structure-group that is closest to them.
     */
    public static void load() {
        Set<Integer> visitedGroups = new HashSet<>();
        int groupSize = StructureGroups.getGroupIds().size();
        int [][] grps = StructureGroups.getGroupMap();
        siteCenters = new HashMap<>();

        // Iterate over all sites, find the closest group and move the site there.
        for (Site site : WorldSites.getSites().values()) {
            if (!typeToStruct.containsKey(site.getType())) continue;
            StructureGroup group = findClosestGroup(site, grps, visitedGroups);
            if (group == null) {
                Log.warn("SiteCenters", "Could not find close group for site " + site + "@" + site.getCoords());
                continue;
            }

            // Move the site to that group.
            visitedGroups.add(group.getId());
            siteCenters.put(site.getId(), group.getCenter());

            // Stop when no more groups are available.
            if (visitedGroups.size() == groupSize) break;
        }

        // Save the sites with the new coordinates
        try (Output output = new Output(new FileOutputStream(BuildFiles.getSiteCenters()))) {
            Uristmaps.kryo.writeObject(output, siteCenters);
        } catch (FileNotFoundException e) {
            Log.error("SiteCenters", "Could not write site centers file.");
            if (Log.DEBUG) Log.debug("SiteCenters", "Exception", e);
        }
    }

    /**
     * Find a valid group for a site that is closest to it and not yet associated with another site.
     * @param site
     * @param grps
     * @param blacklistedGrps
     * @return The structure group or null if nothing could be found.
     */
    private static StructureGroup findClosestGroup(Site site, int[][] grps, Set<Integer> blacklistedGrps) {
        int radius = 0;

        StructureGroup currentGrp;
        while (true) {
            if (radius >= MAX_SEARCH_RADIUS) break;
            Collection<Integer> groups = getGroupsInRing(site.getCoords(), radius, grps);
            for (int grpId : groups) {
                if (blacklistedGrps.contains(grpId)) continue;
                currentGrp = StructureGroups.getGroup(grpId);

                // If this structure does not find the site type, keep searching.
                if (!typeToStruct.get(site.getType()).equalsIgnoreCase(currentGrp.getType())) {
                    continue;
                }

                // This group is a good match!
                return currentGrp;
            }

            radius += 1;
        }

        return null;
    }

    private static Collection<Integer> getGroupsInRing(Coord2 coords, int radius, int[][] grps) {
        int x = coords.X();
        int y = coords.Y();
        Set<Integer> foundGroups = new LinkedHashSet<>();

        for (int i = 0; i <= radius; i++) {
            try {
                foundGroups.add(grps[x - i][y - (radius - i)]);
            } catch (IndexOutOfBoundsException e) {} // Ignore bounds leaving
            try {
                foundGroups.add(grps[x - i][y + (radius - i)]);
            } catch (IndexOutOfBoundsException e) {} // Ignore bounds leaving
            try {
                foundGroups.add(grps[x + i][y - (radius - i)]);
            } catch (IndexOutOfBoundsException e) {} // Ignore bounds leaving
            try {
                foundGroups.add(grps[x + i][y + (radius - i)]);
            } catch (IndexOutOfBoundsException e) {} // Ignore bounds leaving
        }
        foundGroups.remove(new Integer(0)); // Remove 0 as it represents no group
        return foundGroups;
    }

    public static Map<Integer, Coord2> getCenters() {
        if (siteCenters == null) {
            try (Input input = new Input(new FileInputStream(BuildFiles.getSiteCenters()))) {
                siteCenters = Uristmaps.kryo.readObject(input, HashMap.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return siteCenters;
    }
}
