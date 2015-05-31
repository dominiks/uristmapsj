package org.uristmaps.tasks;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.util.FileFinder;

/**
 * Created by dominik on 01.06.2015.
 */
public class BiomeSatRendererTask extends Task {
    @Override
    public void work() {
        Log.info("BiomeRenderer", "Look at me, working!");
    }

    @Override
    public String getName() {
        return "BiomeRenderer";
    }

    @Override
    public String[] getDependendFiles() {
        return new String[]{
                FileFinder.getBiomeInfo().getAbsolutePath()
        };
    }
}
