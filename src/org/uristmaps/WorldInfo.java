package org.uristmaps;

/**
 * Data container for information about the DF world that is being rendered.
 */
public class WorldInfo {

    private long size;
    private String name;

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
