package org.uristmaps.tasks;

import org.uristmaps.TemplateRenderer;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.OutputFiles;

import java.io.File;

/**
 * Task to call the index compiler.
 */
public class CompileIndexTask extends Task {
    @Override
    public void work() {
        TemplateRenderer.compileIndexHtml();
    }

    @Override
    public File[] getDependendFiles() {
        return new File[] {
                BuildFiles.getBiomeInfo(),
                BuildFiles.getSitesFile(),
                BuildFiles.getWorldFile(),
                BuildFiles.getSiteCenters()
        };
    }

    @Override
    public File[] getTargetFiles() {
        return new File[]{OutputFiles.getIndexHtml()};
    }

    @Override
    public String getName() {
        return "CompileIndex";
    }
}
