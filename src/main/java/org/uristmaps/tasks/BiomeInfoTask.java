package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.util.FileFinder;

/**
 * Created by dominik on 01.06.2015.
 */
public class BiomeInfoTask extends Task {

    @Override
    public void work() {
        Log.info("BiomeInfoTask", "Hey look at me, working!");
    }

    @Override
    public String getName() {
        return "BiomeInfoTask";
    }

    @Override
    public String[] getDependendFiles() {
        return new String[] {
                FileFinder.getBiomeMap().getAbsolutePath()
        };
    }

    @Override
    public String[] getTargetFiles() {
        return new String[] {
                FileFinder.getBiomeInfo().getAbsolutePath()
        };
    }
}
