package org.uristmaps;

import org.uristmaps.data.Coord2;
import org.uristmaps.data.Coord2Mutable;
import org.uristmaps.data.WorldInfo;

import java.util.*;

/**
 * Created by dominik on 02.06.2015.
 */
public class StructureGroups {

    /**
     * Maps site types to structure objects that are associated with them.
     */
    private static Map<String, String> typeToStruct;

    /**
     * Types of structures that are ignored.
     * Usually because there is not site icon for them.
     */
    private static Set<String> structBlacklist;

    /**
     * Maps coordinates to group ids.
     */
    private static int[][] groups;

    /**
     * Maps group ids to group types.
     */
    private static Map<Integer, String> groupTypes;

    static {
        typeToStruct = new HashMap<>();
        typeToStruct.put("hamlet", "village");
        typeToStruct.put("dark fortress", "castle");
        typeToStruct.put("dark pits", "castle");
        typeToStruct.put("tomb", "castle");
        typeToStruct.put("hillocks", "village");
        typeToStruct.put("town", "village");
        typeToStruct.put("forest retreat", "village");

        structBlacklist = new HashSet<>(Arrays.asList(
                "river", "meadow", "crops", "orchard", "pasture", "road"
        ));
    }

    /**
     * Iterate over all coordinates and make groups of connected structures.
     */
    public static void load() {
        int size = WorldInfo.getSize();

        groups = new int[size][size];
        groupTypes = new HashMap<>();

        String currentType;
        Coord2 checkCoord;
        int currentGrp;
        int nextGrpId = 1;
        int cX;
        int cY;

        // Bitmap to keep track of all tiles that have been visited already.
        // Tiles might be visited before they are reached by the loops by
        // being targeted in a group flood-fill.
        boolean[][] visited = new boolean[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (visited[x][y]) continue;
                currentType = StructureInfo.getData(x,y);
                if (currentType == null || structBlacklist.contains(currentType)) continue;

                currentGrp = groups[x][y];
                if (currentGrp != 0) continue;

                groupTypes.put(nextGrpId, currentType);
                groups[x][y] = nextGrpId;
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
                    if (groups[cX][cY] != 0) continue; // Allready added to a group

                    // nothing there
                    if (StructureInfo.getData(cX, cY) != null
                            && StructureInfo.getData(cX, cY).equalsIgnoreCase(currentType)) continue;

                    // Add this tile to the group
                    groups[cX][cY] = currentGrp;

                    // add all neighbours to check
                    addToCheck(cX+1,   cY, visited, toVisit);
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
            toVisit.add(new Coord2(x, y));
        } catch (ArrayIndexOutOfBoundsException e) {
            // Just fail silently, don't care if we were trying to add an out of bounds coordinate.
        }
    }
}
