package org.uristmaps.data;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A single site within the world.
 */
public class Site {

    private int id;
    private String name;
    private String nameEnglish;
    private String type;
    private String owner;
    private String parentCiv;
    private Coord2 coords;

    private double lat;
    private double lon;

    private Map<String, Integer> populations;

    public Site() {}

    public Site(int id) {
        this.id = id;
        populations = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getParentCiv() {
        return parentCiv;
    }

    public void setParentCiv(String parentCiv) {
        this.parentCiv = parentCiv;
    }

    public Map<String, Integer> getPopulations() {
        return populations;
    }

    public void setPopulation(String name, int size) {
        populations.put(name, size);
    }

    /**
     * Add the info contained in the provided line to the site.
     *
     * Line is something like on of these:
     *     1: Ostukurrith, "Sneakedscar", cave
     *     Owner: Trulbin, kobolds
     *     Parent Civ: Jlorsnin, kobolds
     *     255 kobolds
     * @param line
     */
    public void addInfo(String line) {
        if (line.contains("\"")) {
            String[] split = line.split(":")[1].split(",");
            setName(WordUtils.capitalize(split[0].trim()));
            setNameEnglish(WordUtils.capitalize(split[1].trim().replace("\"", "")));
            setType(WordUtils.capitalize(split[2].trim()));
        } else if (line.contains(":")) {
            String[] split = line.split(":");
            if (split[0].equals("Owner")) {
                setOwner(WordUtils.capitalize(split[1].trim()));
            } else if (split[0].equals("Parent Civ")) {
                setParentCiv(WordUtils.capitalize(split[1].trim()));
            }
        } else {
            int separator = line.indexOf(" ");
            setPopulation(WordUtils.capitalize(line.substring(separator).trim()), Integer.parseInt(line.substring(0, separator)));
        }

    }

    public Coord2 getCoords() {
        return coords;
    }

    public void setCoords(Coord2 coords) {
        this.coords = coords;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return getName() + "(" + getType() + ")";
    }

    public Map<Integer, String> getSortedPopulations() {
        TreeMap<Integer, String> result = new TreeMap<>();
        for (String popName : getPopulations().keySet()) {
            result.put(populations.get(popName), popName);
        }
        return result;
    }
}
