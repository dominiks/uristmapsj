package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of fileMap' size and crc to see if they have changed since the last run.
 *
 * When you have a collection of files you work on, you ask this object
 * if there are any files that need to be redone (are dirty). Then do your work
 * and tell the watcher to update the state for these files.
 */
public class FileWatcher {

    /**
     * The collection of files and their last known state.
     */
    private Map<String, FileInfo> fileMap;

    /**
     * Create a new filewatcher. Will automatically try to read the stored info.
     */
    public FileWatcher() {
        // Try to read the file-info file.
        File storeFile = FileFinder.getFileStore();
        if (storeFile.exists()) {

            try (Input input = new Input(new FileInputStream(storeFile))) {
                fileMap = Uristmaps.kryo.readObject(input, HashMap.class);
            } catch (FileNotFoundException e) {
                Log.warn("FileWatcher", "Error when reading state file: " + storeFile);
                if (Log.DEBUG) Log.debug("FileWatcher", "Exception", e);
                fileMap = new HashMap<>();
            }

            fileMap = new HashMap<>();
        }
    }

    /**
     * Check if any of the given files is missing or has no/wrong state info stored.
     * @param files
     * @return
     */
    public boolean allOk(File[] files) {
        for (File f : files) {
            if (!fileMap.containsKey(f.getAbsolutePath())) return false;
            if (!f.exists()) return false;
            if (f.length() != fileMap.get(f.getAbsolutePath()).getSize()) return false;
        }
        return true;
    }

    /**
     * Filter all "dirty" files from the provided files.
     * @param files Files that are to be checked.
     * @return All files that are either missing or have no/wrong data stored.
     */
    public File[] getDirty(File[] files) {
        List<File> result = new LinkedList<>();
        for (File f : files) {
            if (!fileMap.containsKey(f.getAbsolutePath())) result.add(f);
            if (!f.exists()) result.add(f);
            if (f.length() != fileMap.get(f.getAbsolutePath()).getSize()) result.add(f);
        }
        return result.toArray(new File[]{});
    }

    /**
     * Add the current state of the provided files to the store.
     * @param files
     */
    public void updateFiles(File[] files) {
        for (File f : files) {
            fileMap.put(f.getAbsolutePath(), new FileInfo(f));
        }
        saveFile();
    }

    public void saveFile() {
        File storeFile = FileFinder.getFileStore();
        try (Output output = new Output(new FileOutputStream(storeFile))) {
            Uristmaps.kryo.writeObject(output, fileMap);
        } catch (FileNotFoundException e) {
            Log.warn("FileWatcher", "Error when writing state file: " + storeFile);
            if (Log.DEBUG) Log.debug("FileWatcher", "Exception", e);
        }
    }

}
