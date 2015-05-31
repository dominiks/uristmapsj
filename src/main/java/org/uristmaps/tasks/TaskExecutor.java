package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A task executor has a list of tasks, then gets told which tasks to execute.
 * Finds then out what tasks are needed to run before.
 */
public class TaskExecutor {

    /**
     * Tasks that are managed by this executor.
     */
    private Map<String, Task> tasks = new HashMap<>();

    /**
     * Maps files to the task that creates them.
     */
    private Map<File, String> filesCreatedByTask = new HashMap<>();

    /**
     * Tasks that have been executed in this run.
     * Prevents duplicate runs.
     */
    private HashSet<String> executedTasks;

    /**
     * Create an empty TaskExecutor.
     */
    public TaskExecutor() {};

    /**
     * Add a task to this executor.
     * Does not execute that task! Call exec(taskname) to start that task.
     * @param task
     */
    public void addTask(Task task) {
        if (tasks.containsKey(task.getName())) {
            if (Log.DEBUG) Log.debug("TaskExecutor", "Skipped duplicate task: " + task.getName());
            return;
        }
        tasks.put(task.getName(), task);
    }

    /**
     * Execute the provided tasks in the provided order.
     */
    public void exec(String... tasksToRun) {
        // Keeps track of tasks that have been executed in this run
        executedTasks = new HashSet<>();

        for (String taskName : tasksToRun) {
            // Skip the task if it has been run as a dependency for a previous task
            if (executedTasks.contains(taskName)) continue;

            // Execute the task and add it to the completed tasks.
            Task task = tasks.get(taskName);
            execTask(task, false);
        }

        // All files that have been looked at now need to have their current state saved for this run.
        Set<File> processedFiles = new HashSet<>();
        for (String taskName : executedTasks) {
            for (String fileName : tasks.get(taskName).getDependendFiles()) {
                processedFiles.add(new File(fileName));
            }
        }
        Uristmaps.files.updateFiles(processedFiles.toArray(new File[]{}));
    }

    /**
     * Executes a single task. Find which files it needs and run the tasks that create them.
     * @param task
     */
    private void execTask(Task task, boolean taskMustRun) {
        Set<String> tasksNeeded = new HashSet<String>();


        // If this task does not have to run, check if it needs to run
        boolean runIt = taskMustRun;
        // Iterate over all files to see if this task needs to be run.
        for (String fileName : task.getDependendFiles()) {
            File file = new File(fileName);
            if (!file.exists()) {
                // Check if there is a provider task for this missing file, else crash
            } else {
                // Check if this file has changed since the last run
            }
        }

        // Run all tasks that have to run before this.
        for (String taskName : tasksNeeded) {
            if (executedTasks.contains(taskName)) continue;
            execTask(tasks.get(taskName), false);
        }

        // Now run it. But only if it needs to.
        if (runIt) {
            task.work();
        }
        // Add it to the log of completed task.
        executedTasks.add(task.getName());
    }

}
