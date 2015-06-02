package org.uristmaps.util;

import java.io.File;

import static org.uristmaps.Uristmaps.conf;

/**
 * Created by dominik on 01.06.2015.
 */
public class BuildFiles {

    /**
     * Return the path to the file-state file.
     * Contains a Map<String, FileInfo> object.
     * @return
     */
    public static File getFileStore() {
        return new File(conf.fetch("Paths", "build"), "files.kryo");
    }

    /**
     * Contains a WorldInfo object.
     * @return
     */
    public static File getWorldFile() {
        return new File(conf.fetch("Paths", "build"), "worldinfo.kryo");
    }

    public static File getSitesFile() {
        return new File(conf.fetch("Paths", "build"), "sites.kryo");
    }

    /**
     * The biome data file. Contains a String[][] array that maps x,y to biome name.
     * @return
     */
    public static File getBiomeInfo() {
        File result = new File(conf.fetch("Paths", "build"), "biomes.kryo");
        return result;
    }

    public static File getStructureGroups() {
        return new File(conf.fetch("Paths", "build"), "struct_groups.kryo");
    }
}
