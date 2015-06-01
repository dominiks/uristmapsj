package org.uristmaps.tasks;

import org.uristmaps.renderer.SatRenderer;
import org.uristmaps.util.BuildFiles;

/**
 * Task to integrate the biome sat renderer into the task framework.
 *
 * Uses no target files. The target tile-files are managed by the renderer itself to
 * check which zoom level can be skipped.
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
                BuildFiles.getBiomeInfo().getAbsolutePath(),
                BuildFiles.getWorldFile().getAbsolutePath()
        };
    }
}
