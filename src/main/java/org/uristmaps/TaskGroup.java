package org.uristmaps;

import org.uristmaps.tasks.Task;

import java.util.Collection;

/**
 * Created by dominik on 03.06.2015.
 */
public abstract class TaskGroup {

    public abstract Collection<Task> getTasks();

    public abstract Collection<String> getTaskNames();

    public abstract String getName();
}
