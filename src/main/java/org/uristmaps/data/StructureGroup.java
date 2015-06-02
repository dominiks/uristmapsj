package org.uristmaps.data;

import java.util.LinkedList;
import java.util.List;

/**
 * A group of structures of the same type.
 */
public class StructureGroup {
    private int id;
    private String type;

    private List<Coord2> points = new LinkedList<>();

    /**
     * Empty constructor for kryo.
     */
    public StructureGroup() {}

    /**
     * Create a new group with the given id and type.
     * @param id
     * @param type
     */
    public StructureGroup(int id, String type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Get the list of points that belong to this group.
     * @return
     */
    public List<Coord2> getPoints() {
        return points;
    }

    /**
     * Add a point to this group.
     * @param p
     */
    public void addPoint(Coord2 p) {
        points.add(p);
    }

    /**
     * Calculate the center coordinate of this group.
     * @return
     */
    public Coord2 getCenter() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Coord2 p : points) {
            if (p.X() < minX) minX = p.X();
            if (p.X() > maxX) maxX = p.X();
            if (p.Y() < minY) minY = p.Y();
            if (p.Y() > maxY) maxY = p.Y();
        }

        return new Coord2((minX + maxX) / 2, (minY + maxY) / 2);
    }

    public String getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return getType() + "#" + getId();
    }
}
