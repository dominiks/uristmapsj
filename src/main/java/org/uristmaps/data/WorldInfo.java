package org.uristmaps.data;

/**
 * Data container for information about the DF world that is being rendered.
 */
public class WorldInfo {

    private int size;
    private String name;

    public WorldInfo(int size) {
        this.size = size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
