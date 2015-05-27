package org.uristmaps;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import org.ini4j.Wini;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.renderer.LayerRenderer;
import org.uristmaps.renderer.SatRenderer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by schacht on 26.05.15.
 */
public class Uristmaps {

    public static Wini conf;

    public static Kryo kryo;

    public static void main(String[] args) {
        // Load configuration file
        Log.info("Uristmaps v0.3");
        loadConfig();
        initKryo();
        initLogger();

        // TODO: Set logger to debug if flag is set in config
        if (conf.get("App", "debug", Boolean.class)) {
            Log.DEBUG();
            Log.info("Enabled Debug Logging");
        }

        // Compile Tilesets
        Tilesets.compile();


        // TODO: Load world info
        // TODO: Load sites info
        // TODO: Load biome info
        // TODO: Load structures info
        // TODO: Load detailed site maps
        // TODO: Load regions info

        // TODO: Render biome tiles
        // TODO: Render region labels
        // TODO: Place region labels
        // TODO: Place site labels
        // TODO: Place detailed site maps

        // TODO: Create JS files
        // TODO: Assemble output resources

        System.exit(0);
        int level = 4;

        WorldInfo worldInfo = new WorldInfo(2050);
        RenderSettings renderSettings = new RenderSettings(level, worldInfo);

        LayerRenderer renderer = new SatRenderer(renderSettings, worldInfo);

        for (int x = 0; x < Math.pow(2, level); x++) {
            for (int y = 0; y < Math.pow(2, level); y++) {
                renderer.renderMapTile(x, y);
            }
        }
    }

    private static void initLogger() {
        Log.setLogger(new FilteringLogger(conf.get("App").getAll("log_blacklist")));
    }

    private static void initKryo() {
        kryo = new Kryo();
        kryo.register(Coord2.class);
    }

    /**
     * Load the config ini-style file.
     */
    private static void loadConfig() {
        // TODO: Read config file path from ARGS if provided.
        File targetFile = new File("config.cfg");
        try {
            conf = new Wini(targetFile);
        } catch (IOException e) {
            if (!targetFile.exists()){
                Log.error("Could not find the config file: " + targetFile);
            }
            System.exit(1);
        }
        Log.info("Found config file.");
    }

    /**
     * A logger that can ignore items belonging to a set category.
     */
    static public class FilteringLogger extends Log.Logger {

        private final Set<String> blacklist;

        /**
         * Create a new filtering logger with the provided blacklist.
         * @param blacklist
         */
        public FilteringLogger(List<String> blacklist) {
            if (blacklist == null) {
                this.blacklist = new HashSet<>();
            } else {
                this.blacklist = new HashSet<String>(blacklist);
            }
        }

        /**
         * Drop log messages from blacklisted categories.
         * @param level
         * @param category
         * @param message
         * @param ex
         */
        public void log (int level, String category, String message, Throwable ex) {
            if (blacklist.contains(category)) {
                return;
            }
            super.log(level, category, message, ex);
        }
    }
}


