package org.uristmaps.tasks;

import org.uristmaps.data.WorldInfo;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFilesFinder;

/**
 * Created by dominik on 01.06.2015.
 */
public class WorldInfoTask extends Task {

    @Override
    public void work() {
        WorldInfo.load();
    }

    @Override
    public String getName() {
        return "WorldInfo";
    }

    @Override
    public String[] getDependendFiles() {
        return new String[] {
                ExportFilesFinder.getWorldHistory().getAbsolutePath(),
                ExportFilesFinder.getBiomeMap().getAbsolutePath()};
    }

    @Override
    public String[] getTargetFiles() {
        return new String[] {BuildFiles.getWorldFile().getAbsolutePath()};
    }
}
