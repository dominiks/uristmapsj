package org.uristmaps.tasks;

import org.uristmaps.TaskGroup;
import org.uristmaps.Uristmaps;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Taskgroup for rendering the sat-layers.
 */
public class SatRendererTaskGroup extends TaskGroup {

    @Override
    public Collection<Task> getTasks() {
        List<Task> result = new LinkedList<>();
        int minLevel = Uristmaps.conf.get("Map", "min_zoom", Integer.class);
        int maxLevel = Uristmaps.conf.get("Map", "max_zoom", Integer.class);

        for (int level = minLevel; level <= maxLevel; level++) {
            result.add(new BiomeSatRendererTask(level));
        }
        return result;
    }

    @Override
    public Collection<String> getTaskNames() {
        List<String> result = new LinkedList<>();
        int minLevel = Uristmaps.conf.get("Map", "min_zoom", Integer.class);
        int maxLevel = Uristmaps.conf.get("Map", "max_zoom", Integer.class);

        for (int level = minLevel; level <= maxLevel; level++) {
            result.add("BiomeRenderer:" + level);
        }
        return result;
    }

    @Override
    public String getName() {
        return "BiomeRenderer";
    }
}
