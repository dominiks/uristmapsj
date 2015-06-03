package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.TaskGroup;
import org.uristmaps.Tilesets;
import org.uristmaps.Uristmaps;
import org.uristmaps.util.BuildFiles;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dominik on 03.06.2015.
 */
public class TilesetsTaskGroup extends TaskGroup {
    @Override
    public Collection<Task> getTasks() {
        List<Task> result = new LinkedList<>();
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

            result.add(new AdhocTask("TilesetTask:" + tileSize,
                    tileDir.listFiles(pathname -> pathname.getName().endsWith(".png")),
                    new File[] {BuildFiles.getTilesetImage(tileSize),
                                BuildFiles.getTilesetIndex(tileSize)
                    },
                    () -> Tilesets.compileDirectory(tileDir))
            );
        }



        return result;
    }

    @Override
    public Collection<String> getTaskNames() {
        List<String> result = new LinkedList<>();
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

            result.add("TilesetTask:" + tileSize);
        }
        return result;
    }

    @Override
    public String getName() {
        return "TilesetTask";
    }
}
