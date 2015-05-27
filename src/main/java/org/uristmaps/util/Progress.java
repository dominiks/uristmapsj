package org.uristmaps.util;

import com.esotericsoftware.minlog.Log;

/**
 * Created by dominik on 27.05.2015.
 */
public abstract class Progress {

    /**
     * The maximum value of work units.
     */
    protected int max;

    /**
     * The current worked units.
     */
    protected int current;

    /**
     * The category to log as.
     */
    private String category;

    /**
     * The log level to use.
     */
    private int loglevel;

    public Progress(int max, String category, int logLevel) {
        this.max = max;
        this.category = category;
        this.loglevel = logLevel;
    }

    public abstract void show();

    public abstract void show(int val);

    /**
     * Print the current percentage.
     */
    public void report() {
        int percent = (int)((float) current / max * 100);
        StringBuilder msg = new StringBuilder();
        msg.append(percent).append("% (").append(current).append("/").append(max).append(")");
        if (loglevel == Log.LEVEL_DEBUG) {
            Log.debug(category, msg.toString());
        } else if (loglevel == Log.LEVEL_INFO) {
            Log.info(category, msg.toString());
        }
    }
}
