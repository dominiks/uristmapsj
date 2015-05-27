package org.uristmaps.util;

import com.esotericsoftware.minlog.Log;

/**
 * Progress printer. Reports every x-Percent
 */
public class StepProgress extends Progress {

    /**
     * The maximum value of work units.
     */
    private int max;

    /**
     * The current worked units.
     */
    private int current;

    /**
     * At which percentage step the progress will report.
     */
    private int step;

    /**
     * The category to log as.
     */
    private String category;

    /**
     * The log level to use.
     */
    private int loglevel;

    /**
     * When the last call of show() was.
     */
    private int nextPercent;

    /**
     * Create a new StepProgress object.
     * @param max
     * @param step How many percent have to pass until output.
     * @param category The category to log as. Can be null.
     * @param loglevel The log level to use.
     */
    public StepProgress(int max, int step, String category, int loglevel) {
        super(max, category, loglevel);
        this.step = step;
    }

    /**
     * Creates a new StepProgress object with the log level INFO.
     * @param max
     * @param step
     * @param category
     */
    public StepProgress(int max, int step, String category) {
        this(max, step, category, Log.LEVEL_INFO);
    }

    /**
     * Call when one unit has completed.
     */
    @Override
    public void show() {
        this.show(current+1);
    }

    /**
     * Call when more than 1 unit have been completed.
     * @param val
     */
    @Override
    public void show(int val) {
        current = val;

        int percent = (int)((float) current / max * 100);
        if (percent >= nextPercent) {
            nextPercent = (percent / step) * step + step;
            report();
        } else if (current == max) {
            report();
        }
    }

}
