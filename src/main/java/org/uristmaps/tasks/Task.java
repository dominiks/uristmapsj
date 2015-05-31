package org.uristmaps.tasks;

/**
 * Base class for tasks.
 */
public abstract class Task {

    /**
     * The array of files that this task needs for it to run.
     * @return
     */
    public String[] getDependendFiles() {
        return new String[]{};
    }

    /**
     * The array of files that are produced by this task.
     * @return
     */
    public String[] getTargetFiles() {
        return new String[]{};
    }

    /**
     * Get an array of task names that have to be executed before this one.
     * @return
     */
    public String[] getDependantTasks() {
        return new String[]{};
    }

    /**
     * Do your thing, little task!
     */
    public abstract void work();

    /**
     * The name of the task.
     * @return
     */
    public abstract String getName();
}
