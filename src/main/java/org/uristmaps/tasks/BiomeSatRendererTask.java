package org.uristmaps.tasks;

import org.uristmaps.renderer.SatRenderer;
import org.uristmaps.util.FileFinder;

/**
 * Task to integrate the biome sat renderer into the task framework
 */
public class BiomeSatRendererTask extends Task {
    @Override
    public void work() {
        SatRenderer renderer = new SatRenderer();
        renderer.work();
    }

    @Override
    public String getName() {
        return "BiomeRenderer";
    }

    @Override
    public String[] getDependendFiles() {
        return new String[]{
                FileFinder.getBiomeInfo().getAbsolutePath(),
                FileFinder.getWorldFile().getAbsolutePath()
        };
    }
}
