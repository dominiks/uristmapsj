package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Can copy files around.
 */
public class FileCopier {

    /**
     * Copy the contents of the res/ folder into the specified output.
     */
    public static void distResources() {
        Log.info("FileCopier", "Copying static resources to output.");

        try {
            copyFilesOfDirTo(new File("res"), new File((Uristmaps.conf.fetch("Paths", "output"))));
            FileUtils.copyDirectory(new File(Uristmaps.conf.fetch("Paths", "tiles"), "16"),
                    new File(Uristmaps.conf.fetch("Paths", "output"), "biome_legend"));
        } catch (IOException e) {
            Log.error("FileCopier", "Failed to copy the files: " + e.getMessage());
            if (Log.DEBUG) Log.debug("FileCopier", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * Copy the contents of a directory recursively into another one.
     * @param src The contents of this directory will be copied.
     * @param dst Into this directory.
     * @throws IOException When copying fails.
     */
    public static void copyFilesOfDirTo(File src, File dst) throws IOException {
        for (File file : src.listFiles()) {
            if (file.isDirectory()) {
                // Recursive into this directory
                File newDst = new File(dst, file.getName());
                copyFilesOfDirTo(file, newDst);
            } else {
                Log.debug("FileCopier", "Copying file " + file + " -> " + dst);
                FileUtils.copyFileToDirectory(file, dst);
            }
        }

    }
}
