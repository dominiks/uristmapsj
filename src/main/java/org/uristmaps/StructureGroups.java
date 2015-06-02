package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.StructureGroup;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.util.BuildFiles;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Created by dominik on 02.06.2015.
 */
public class StructureGroups {

    /**
     * Types of structures that are ignored.
     * Usually because there is not site icon for them.
     */
    private static Set<String> structBlacklist;

    /**
     * Maps coordinates to group ids.
     */
    private static int[][] groupMap;

    /**
     * Maps group ids to group types.
     */
    private static Map<Integer, StructureGroup> groups;

    static {
        structBlacklist = new HashSet<>(Arrays.asList(
                "river", "meadow", "crops", "orchard", "pasture", "road"
        ));
    }

    /**
     * Iterate over all coordinates and make groupMap of connected structures.
     */
    public static void load() {
        // Try to load the kryo files
        if (BuildFiles.getStructureGroups().exists() && BuildFiles.getStructureGroupsDefinitions().exists()) {
            Log.debug("StructureGroups", "Reading groupMap from build dir.");
            loadFromStore();
            return;
        }

        Log.debug("StructureGroups", "Reading groupMap from exported files.");
        int size = WorldInfo.getSize();

        groupMap = new int[size][size];
        groups = new HashMap<>();

        String currentType;
        Coord2 checkCoord;
        int currentGrp;
        int nextGrpId = 1;
        int cX;
        int cY;
        StructureGroup lastAdded;

        // Bitmap to keep track of all tiles that have been visited already.
        // Tiles might be visited before they are reached by the loops by
        // being targeted in a group flood-fill.
        boolean[][] visited = new boolean[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (visited[x][y]) continue;
                currentType = StructureInfo.getData(x,y);
                if (currentType == null || structBlacklist.contains(currentType)) continue;

                currentGrp = groupMap[x][y];
                if (currentGrp != 0) continue;

                lastAdded = new StructureGroup(nextGrpId, currentType);
                lastAdded.addPoint(new Coord2(x,y));
                groups.put(nextGrpId, lastAdded);

                groupMap[x][y] = nextGrpId;
                currentGrp = nextGrpId;
                nextGrpId += 1;

                // From here, make a group fill to add all structures of this type to the current group.
                LinkedList<Coord2> toVisit = new LinkedList<>();
                toVisit.add(new Coord2(x+1,   y));
                toVisit.add(new Coord2(x  , y+1));
                toVisit.add(new Coord2(x+1, y+1));
                while (!toVisit.isEmpty()) {
                    checkCoord = toVisit.pop();
                    cX = checkCoord.X();
                    cY = checkCoord.Y();
                    visited[cX][cY] = true;
                    int i = groupMap[cX][cY];
                    if (i == 0) continue; // Allready added to a group

                    // nothing there
                    String struct = StructureInfo.getData(cX, cY);
                    if (struct != null
                            && StructureInfo.getData(cX, cY).equalsIgnoreCase(currentType)) continue;

                    // Add this tile to the group
                    groupMap[cX][cY] = currentGrp;
                    lastAdded.addPoint(new Coord2(cX,cY));

                    // Add all neighbours to check
                    addToCheck(cX + 1, cY, visited, toVisit);
                    addToCheck(cX  , cY+1, visited, toVisit);
                    addToCheck(cX+1, cY+1, visited, toVisit);
                    addToCheck(cX-1,   cY, visited, toVisit);
                    addToCheck(cX  , cY-1, visited, toVisit);
                    addToCheck(cX-1, cY-1, visited, toVisit);
                    addToCheck(cX-1, cY+1, visited, toVisit);
                    addToCheck(cX+1, cY-1, visited, toVisit);
                }
            }
        }

        // Store the group info
        try (Output output = new Output(new FileOutputStream(BuildFiles.getStructureGroups()))) {
            Uristmaps.kryo.writeObject(output, groupMap);
        } catch (FileNotFoundException e) {
            Log.error("StructureGroups", "Could not write file: " + BuildFiles.getStructureGroups());
            if (Log.DEBUG) Log.debug("StructureGroups", "Exception", e);
            System.exit(1);
        }

        try (Output output = new Output(new FileOutputStream(BuildFiles.getStructureGroupsDefinitions()))) {
            Uristmaps.kryo.writeObject(output, groups);
        } catch (FileNotFoundException e) {
            Log.error("StructureGroups", "Could not write file: " + BuildFiles.getStructureGroupsDefinitions());
            if (Log.DEBUG) Log.debug("StructureGroups", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Load the groupMap and group definitions from the build directory.
     */
    private static void loadFromStore() {
        try (Input input = new Input(new FileInputStream(BuildFiles.getStructureGroups()))) {
            groupMap = Uristmaps.kryo.readObject(input, int[][].class);
        } catch (FileNotFoundException e) {
            Log.error("StructureGroups", "Could not read file: " + BuildFiles.getStructureGroups());
            if (Log.DEBUG) Log.debug("StructureGroups", "Exception", e);
            System.exit(1);
        }

        try (Input input = new Input(new FileInputStream(BuildFiles.getStructureGroupsDefinitions()))) {
            groups = Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (FileNotFoundException e) {
            Log.error("StructureGroups", "Could not write file: " + BuildFiles.getStructureGroupsDefinitions());
            if (Log.DEBUG) Log.debug("StructureGroups", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Retrieve the group data.
     * @return
     */
    public static int[][] getGroupMap() {
        if (groupMap == null) load();
        return groupMap;
    }

    /**
     * Retrieve the id of the group.
     * @param grpId
     * @return
     */
    public static StructureGroup getGroup(int grpId) {
        if (groups == null) load();
        return groups.get(grpId);
    }

    /**
     * Adds a coordinate-pair to the toVisit list, if it has not yet been visited.
     * @param x
     * @param y
     * @param visited The visitation bitmap
     * @param toVisit List to add to.
     */
    private static void addToCheck(int x, int y, boolean[][] visited, LinkedList<Coord2> toVisit) {
        try {
            if (visited[x][y]) return;
            if (toVisit.contains(new Coord2(x,y))) return;
            toVisit.add(new Coord2(x, y));
        } catch (ArrayIndexOutOfBoundsException e) {
            // Just fail silently, don't care if we were trying to add an out of bounds coordinate.
        }
    }

    public static Set<Integer> getGroupIds() {
        if (groups == null) load();
        return groups.keySet();
    }

    public static Collection<StructureGroup> getGroups() {
        if (groups == null) load();
        return groups.values();
    }
}
