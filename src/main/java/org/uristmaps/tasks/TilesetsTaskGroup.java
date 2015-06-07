package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FilenameUtils;
import org.uristmaps.TaskGroup;
import org.uristmaps.Tilesets;
import org.uristmaps.Uristmaps;
import org.uristmaps.util.BuildFiles;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * DOCME
 */
public class TilesetsTaskGroup extends TaskGroup {

    /**
     * DOCME
     */
    private List<Task> tasks;

    @Override
    public Collection<Task> getTasks() {
        if (tasks == null) generateTasks();
        return tasks;
    }

    /**
     * DOCME
     */
    private void generateTasks() {
        tasks = new LinkedList<>();
        // Iterate over directories in specified tileset dir
        File tilesDir = new File(Uristmaps.conf.fetch("Paths", "tiles")).getAbsoluteFile();

        if (!tilesDir.exists()) {
            Log.error("TilesetTask", "Could not find tiles directory: " + tilesDir);
            System.exit(1);
        }

        for (File tileDir : tilesDir.listFiles(fname -> fname.isDirectory())) {
            int tileSize = 0;
            try {
                tileSize = Integer.parseInt(tileDir.getName());
            } catch (NumberFormatException e) {
                Log.warn("Tileset", "Could not parse size from directory: " + tileDir.getName());
                continue;
            }

            tasks.add(new AdhocTask("TilesetTask:" + tileSize,
                            tileDir.listFiles(pathname -> pathname.getName().endsWith(".png")),
                            new File[] {BuildFiles.getTilesetImage(tileSize),
                                    BuildFiles.getTilesetIndex(tileSize)
                            },
                            () -> Tilesets.compileDirectory(tileDir))
            );
        }

        for (File tileFile : tilesDir.listFiles((dir, name) -> name.endsWith(".txt"))) {
            int tileSize = 0;
            try {
                tileSize = Integer.parseInt(FilenameUtils.removeExtension(tileFile.getName()));
            } catch (NumberFormatException e) {
                Log.warn("Tileset", "Could not parse size from file: " + tileFile.getName());
                continue;
            }

            tasks.add(new AdhocTask("TilesetTask:" + tileSize,
                            new File[] {tileFile},
                            new File[] {BuildFiles.getTilesetColorFile(tileSize)},
                            () -> Tilesets.compileColorTable(tileFile))
            );
        }
    }

    @Override
    public Collection<String> getTaskNames() {
        if (tasks == null) generateTasks();
        List<String> names = new LinkedList<>();
        for (Task task : tasks) {
            names.add(task.getName());
        }
        return names;
    }

    @Override
    public String getName() {
        return "TilesetTask";
    }
}
