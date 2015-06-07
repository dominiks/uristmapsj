package org.uristmaps.tasks;

import org.uristmaps.renderer.SatRenderer;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;
import org.uristmaps.util.OutputFiles;

import java.io.File;

/**
 * Task to integrate the biome sat renderer into the task framework.
 *
 * Uses no target files. The target tile-files are managed by the renderer itself to
 * check which zoom level can be skipped.
 */
public class BiomeSatRendererTask extends Task {

    /**
     * The zoom level to render.
     */
    private final int level;

    /**
     * Create a new render task for the provided zoom level.
     * @param level
     */
    public BiomeSatRendererTask(int level) {
        this.level = level;
    }

    @Override
    public void work() {
        SatRenderer renderer = new SatRenderer(level);
        renderer.work();
    }

    @Override
    public String getName() {
        return "BiomeRenderer:" + level;
    }

    @Override
    public File[] getDependendFiles() {
        return new File[]{
                BuildFiles.getBiomeInfo(),
                BuildFiles.getWorldFile(),
                ExportFiles.getHydroMap(),
                ExportFiles.getStructuresMap()
                // TODO: Add tileset image for the graphics size required for this level
        };
    }

    @Override
    public File[] getTargetFiles() {
        return OutputFiles.getLayerImages("tiles", level);
    }
}
