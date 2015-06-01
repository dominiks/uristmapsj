package org.uristmaps.tasks;

/**
 * Created by dominik on 01.06.2015.
 */
public class AdhocTask extends Task {


    private String name;
    private String[] depFiles;
    private String[] targetFiles;
    private Runnable run;

    /**
     * Create a new adhoc task with the given parameters.
     * @param name
     * @param depFiles Array of dependency files. May be null.
     * @param targetFiles Array of targetFiles. May be null.
     * @param runnable Rn
     */
    public AdhocTask(String name, String[] depFiles, String[] targetFiles, Runnable runnable) {
        this.name = name;
        this.depFiles = depFiles != null ? depFiles: new String[]{};
        this.targetFiles = targetFiles != null ? targetFiles: new String[]{};
        this.run = runnable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getDependendFiles() {
        return depFiles;
    }

    @Override
    public String[] getTargetFiles() {
        return targetFiles;
    }

    @Override
    public void work() {
        run.run();
    }
}
