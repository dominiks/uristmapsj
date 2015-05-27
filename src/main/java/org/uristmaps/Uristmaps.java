package org.uristmaps;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import org.ini4j.Wini;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.Site;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.renderer.LayerRenderer;
import org.uristmaps.renderer.SatRenderer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by schacht on 26.05.15.
 */
public class Uristmaps {

    /**
     * The global configuration object.
     */
    public static Wini conf;

    /**
     * The global kryo object.
     */
    public static Kryo kryo;

    /**
     * Entry point of the application.
     *
     * Runs all available tasks.
     * @param args
     */
    public static void main(String[] args) {
        // Load configuration file
        Log.info("Uristmaps v0.3");
        loadConfig();
        initKryo();
        initLogger();
        initDirectories();

        // TODO: Set logger to debug if flag is set in config
        if (conf.get("App", "debug", Boolean.class)) {
            Log.DEBUG();
            Log.info("Enabled Debug Logging");
        }

        // Compile Tilesets
        //Tilesets.compile();

        // TODO: Load sites info
        WorldSites.load();

        // TODO: Load world info
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

    /**
     * Make sure the output directories exist.
     */
    private static void initDirectories() {
        new File(conf.fetch("Paths", "output")).mkdirs();
        new File(conf.fetch("Paths", "build")).mkdirs();
        new File(conf.fetch("Paths", "tilesets")).mkdirs();
    }

    /**
     * Configure the logger used for output.
     */
    private static void initLogger() {
        Log.setLogger(new FilteringLogger(conf.get("App", "log_blacklist").split(",")));
    }

    /**
     * Configure the kryo instance used for reading/writing objects.
     */
    private static void initKryo() {
        kryo = new Kryo();
        kryo.register(Coord2.class);
        kryo.register(Site.class);
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
        public FilteringLogger(String[] blacklist) {
            if (blacklist == null) {
                this.blacklist = new HashSet<>();
            } else {
                this.blacklist = new HashSet<>();
                for (String item : blacklist) {
                    this.blacklist.add(item.toLowerCase().trim());
                }
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
            if (level == Log.LEVEL_DEBUG && category != null && blacklist.contains(category.toLowerCase())) {
                return;
            }
            super.log(level, category, message, ex);
        }
    }
}


