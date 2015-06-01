package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;

import java.io.File;
import java.util.*;

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
    private Map<String, String> filesCreatedByTask = new HashMap<>();

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
        if (task.getName() == null) {
            Log.error("TaskExecutor", "Task name must not be null!");
            System.exit(1);
        }
        if (tasks.containsKey(task.getName())) {
            if (Log.DEBUG) Log.debug("TaskExecutor", "Skipped duplicate task: " + task.getName());
            return;
        }
        tasks.put(task.getName(), task);

        // Add this task to the files index
        for (String file : task.getTargetFiles()) {
            filesCreatedByTask.put(file, task.getName());
        }
    }

    /**
     * Execute the provided tasks in the provided order.
     */
    public void exec(String... tasksToRun) {
        // Keeps track of tasks that have been executed in this run
        executedTasks = new HashSet<>();

        // Run all provided tasks.
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

        // Note: files that have been created in this run are not needed in the filewatcher
        // because no one is interested in their state. Else they would appear in the
        // dependent files...
    }

    /**
     * Executes a single task. Find which files it needs and run the tasks that create them.
     * @param task
     */
    private void execTask(Task task, boolean taskMustRun) {
        // Maps the task name to the force-flag. When true, the task may not be skipped as its target files are missing.
        Map<String, Boolean> tasksNeeded = new HashMap<>();

        // If this task does not have to run, check if it needs to run
        boolean runIt = taskMustRun;
        // Iterate over all files to see if this task needs to be run.
        for (String fileName : task.getDependendFiles()) {
            File file = new File(fileName);
            if (!file.exists()) {
                // Task needs to run since a file is missing (and hopefully gets recreated)
                runIt = true;
                // Check if there is a provider task for this missing file, else crash
                if (!filesCreatedByTask.containsKey(fileName)) {
                    Log.error("TaskExecutor", "Could not providing task for missing file: " + fileName);
                    System.exit(1);
                }
                tasksNeeded.put(filesCreatedByTask.get(fileName), true);
            } else {
                // Check if this file has changed since the last run
                if (!Uristmaps.files.fileOk(file)) {
                    // If only one file has changed, the task cannot be skipped.
                    runIt = true;

                    /* We cannot break, since other files might be missing and we
                     * would forget to add the providing tasks to the dependencies.
                     */
                }
            }
        }

        // When any of the target files is missing, the task must run
        for (String fileName : task.getTargetFiles()) {
            if (!new File(fileName).exists()) {
                runIt = true;
                break;
            }
        }

        // When the task has no dependencies, just run it.
        if (task.getDependantTasks().length == 0 && task.getDependendFiles().length == 0) {
            runIt = true;
        }

        // Add all tasks that are hardcoded dependencies. These are not forced.
        for (String taskName : task.getDependantTasks()) {
            tasksNeeded.put(taskName, false);
        }

        // Run all tasks that have to run before this.
        for (Map.Entry<String, Boolean> entry : tasksNeeded.entrySet()) {
            if (executedTasks.contains(entry.getKey())) continue;
            execTask(tasks.get(entry.getKey()), entry.getValue());
        }

        // Now run it. But only if it needs to.
        if (runIt) {
            task.work();
            Log.debug("TaskExecutor", "Ran " + task.getName());
        } else {
            Log.debug("TaskExecutor", "Skipping " + task.getName());
        }
        // Add it to the log of completed task.
        executedTasks.add(task.getName());
    }

    /**
     * Add a task with the given parameters.
     * @param name
     * @param depFiles May be null.
     * @param targetFiles May be null.
     * @param work
     */
    public void addTask(String name, String[] depFiles, String[] targetFiles, Runnable work) {
        addTask(new AdhocTask(name, depFiles, targetFiles, work));
    }

    /**
     * Return the unordered set of task names.
     * @return
     */
    public Collection<String> getTasks() {
        return tasks.keySet();
    }

    /**
     * Retrieve a single task by its name.
     * @param name
     * @return
     */
    public Task getTask(String name) {
        return tasks.get(name);
    }
}
