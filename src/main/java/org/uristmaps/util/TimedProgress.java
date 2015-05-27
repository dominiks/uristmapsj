package org.uristmaps.util;

import com.esotericsoftware.minlog.Log;

/**
 * TimedProgress printer. Reports percentage every x seconds
 */
public class TimedProgress extends Progress {



    /**
     * The ms delay that has to pass at least between output updates.
     */
    private int delay;

    /**
     * Measures the ms since the last print.
     */
    private long counter;

    /**
     * When the last call of show() was.
     */
    private long lastShowCall;

    /**
     * Create a new TimedProgress object.
     * @param max
     * @param delay
     * @param category The category to log as. Can be null.
     * @param loglevel The log level to use.
     */
    public TimedProgress(int max, int delay, String category, int loglevel) {
        super(max, category, loglevel);
        this.delay = delay;
    }

    /**
     * Creates a new TimedProgress object with the log level INFO.
     * @param max Amount of units that will be worked.
     * @param delay Milliseconds until next printout.
     * @param category
     */
    public TimedProgress(int max, int delay, String category) {
        this(max, delay, category, Log.LEVEL_INFO);
    }

    /**
     * Call when one unit has completed.
     */
    public void show() {
        this.show(current+1);
    }

    /**
     * Call when more than 1 unit have been completed.
     * @param val
     */
    public void show(int val) {
        current = val;

        long now = System.currentTimeMillis();
        counter += now - lastShowCall;
        System.err.println(counter);

        if (counter >= delay) {
            counter = 0;
            report();
        } else if (current == max) {
            report();
        }
        lastShowCall = now;
    }

}
