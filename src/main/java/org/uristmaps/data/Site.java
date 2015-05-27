package org.uristmaps.data;

import java.util.HashMap;
import java.util.Map;

/**
 * A single site within the world.
 */
public class Site {

    private final int id;
    private String name;
    private String nameEnglish;
    private String type;
    private String owner;
    private String parentCiv;

    private Map<String, Integer> populations;

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
}
