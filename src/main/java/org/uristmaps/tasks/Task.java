package org.uristmaps.tasks;

/**
 * Created by dominik on 26.05.2015.
 */
public abstract class Task {

    public String[] getDependendFiles() {
        return new String[]{};
    }

    public String[] getTargetFiles() {
        return new String[]{};
    }

    public abstract void work();
}
