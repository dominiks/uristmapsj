package org.uristmaps.tasks;

import java.io.File;

/**
 * Base class for tasks.
 *
 * TODO: Allow subtasks that are generated at runtime.
 */
public abstract class Task {

    /**
     * The array of files that this task needs for it to run.
     * @return
     */
    public File[] getDependendFiles() {
        return new File[]{};
    }

    /**
     * The array of files that are produced by this task.
     * @return
     */
    public File[] getTargetFiles() {
        return new File[]{};
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
     * The name of the task used for identification.
     * @return
     */
    public abstract String getName();

    /**
     * Whether this task will be shown to the user when using "list".
     * // TODO: Cannot run this task directly?
     * @return
     */
    public boolean isPublic() {
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }
}
