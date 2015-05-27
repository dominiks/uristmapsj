package org.uristmaps;

/**
 * Some utility stuff that gathered for the project.
 */
public class Util {

    public static final int NOT_FOUND = -1;

    /**
     * Removes the extension from a filename.
     *
     * Adapted from apache commons.io FilenameUtils.
     * @param filename The name of the file containing a file extension.
     * @return The filename without the extension and the dot at the end.
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        final int index = filename.lastIndexOf(".");
        if (index == NOT_FOUND) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

}
