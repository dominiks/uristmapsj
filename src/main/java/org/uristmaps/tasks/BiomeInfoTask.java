package org.uristmaps.tasks;

import org.uristmaps.util.FileFinder;

/**
 * Task to control the parsing of biome data.
 */
public class BiomeInfoTask extends Task {

    @Override
    public void work() {
        ;
    }

    @Override
    public String getName() {
        return "BiomeInfoTask";
    }

    @Override
    public String[] getDependendFiles() {
        return new String[] {
                FileFinder.getBiomeMap().getAbsolutePath()
        };
    }

    @Override
    public String[] getTargetFiles() {
        return new String[] {
                FileFinder.getBiomeInfo().getAbsolutePath()
        };
    }
}
