package org.uristmaps.data;

import org.uristmaps.FileFinder;
import org.uristmaps.Uristmaps;

import java.io.File;

/**
 * Data container for information about the DF world that is being rendered.
 */
public class WorldInfo {

    private int size;
    private String name;

    public WorldInfo() {

    }

    /**
     * Load world info from the export files.
     */
    public void init() {
        // World size will be taken from the biome export map
        loadWorldSize();
    }

    /**
     * Load the biome map and check its px size for world unit-size.
     */
    private void loadWorldSize() {
        // TODO: Implement me
        if (Uristmaps.files.fileOk(FileFinder.getBiomeMap()) && FileFinder.getWorldFile().exists())
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
