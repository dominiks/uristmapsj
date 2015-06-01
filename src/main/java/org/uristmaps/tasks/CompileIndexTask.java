package org.uristmaps.tasks;

import org.uristmaps.TemplateRenderer;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.OutputFiles;

/**
 * Task to call the index compiler.
 */
public class CompileIndexTask extends Task {
    @Override
    public void work() {
        TemplateRenderer.compileIndexHtml();
    }

    @Override
    public String[] getDependendFiles() {
        return new String[] {
                BuildFiles.getBiomeInfo().getAbsolutePath(),
                BuildFiles.getSitesFile().getAbsolutePath(),
                BuildFiles.getWorldFile().getAbsolutePath()
        };
    }

    @Override
    public String[] getTargetFiles() {
        return new String[]{OutputFiles.getIndexHtml().getAbsolutePath()};
    }

    @Override
    public String getName() {
        return "CompileIndex";
    }
}
