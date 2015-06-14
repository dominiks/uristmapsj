package org.uristmaps.tasks;

import java.io.File;

/**
 * Configurable task object. Mainly used for "anonymous" tasks created by the TaskExecutor.
 */
public class AdhocTask extends Task {


    private String name;
    private File[] depFiles;
    private File[] targetFiles;
    private Runnable run;

    /**
     * Create a new adhoc task with the given parameters.
     * @param name
     * @param depFiles Array of dependency files. May be null.
     * @param targetFiles Array of targetFiles. May be null.
     * @param runnable Rn
     */
    public AdhocTask(String name, File[] depFiles, File[] targetFiles, Runnable runnable) {
        this.name = name;
        this.depFiles = depFiles != null ? depFiles: new File[]{};
        this.targetFiles = targetFiles != null ? targetFiles: new File[]{};
        this.run = runnable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public File[] getDependendFiles() {
        return depFiles;
    }

    @Override
    public File[] getTargetFiles() {
        return targetFiles;
    }

    @Override
    public void work() {
        run.run();
    }
}
