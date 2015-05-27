package org.uristmaps;

import java.io.File;

/**
 * Change information for a single file.
 */
public class FileInfo {

    private String path;
    private long size;

    public FileInfo() {};

    /**
     * Create a new entry for the given file.
     * @param file
     */
    public  FileInfo(File file) {
        this.size = file.length();
        this.path = file.getAbsolutePath();
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

}
