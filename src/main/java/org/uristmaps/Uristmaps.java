package org.uristmaps;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.minlog.Log;
import org.ini4j.Wini;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.FileInfo;
import org.uristmaps.data.Site;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.renderer.LayerRenderer;
import org.uristmaps.renderer.SatRenderer;
import org.uristmaps.tasks.BiomeInfoTask;
import org.uristmaps.tasks.BiomeSatRendererTask;
import org.uristmaps.tasks.TaskExecutor;
import org.uristmaps.tasks.WorldInfoTask;
import org.uristmaps.util.FileFinder;
import org.uristmaps.util.FileWatcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by schacht on 26.05.15.
 * TODO: Store the version number somewhere.
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
     * The global file watcher object.
     */
    public static FileWatcher files;

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

        for (String arg : args) {
            if (arg.equalsIgnoreCase("host")) {
                WebServer.start();
                return;
            }
        }

        initKryo();
        initLogger();
        initDirectories();
        initFileInfo();

        // Set logger to debug if flag is set in config
        if (conf.get("App", "debug", Boolean.class)) {
            Log.DEBUG();
            Log.info("Enabled Debug Logging");
        }

        // Fill the executor with all available tasks.
        TaskExecutor executor = new TaskExecutor();
        executor.addTask(new WorldInfoTask());
        executor.addTask("BiomeInfoTask",
                new String[]{ FileFinder.getBiomeMap().getAbsolutePath()},
                new String[]{ FileFinder.getBiomeInfo().getAbsolutePath()},
                () -> BiomeInfo.load());
        executor.addTask(new BiomeSatRendererTask());

        // Parse more parameters
        for (String arg : args) {
            if (arg.equalsIgnoreCase("tasks") ||arg.equalsIgnoreCase("list")) {
                echoTasks(executor);
                return;
            } else if (arg.equalsIgnoreCase("forget")) {
                forgetInputFiles();
                return;
            }
        }

        // Run the default task or the requested task.
        executor.exec("BiomeRenderer");
    }

    private static void forgetInputFiles() {
        System.out.println("Forgetting all file states.");
        files.forget();
    }

    private static void echoTasks(TaskExecutor executor) {
        // List all available tasks
        System.out.println("Available tasks:");
        TreeSet<String> taskNames = new TreeSet<>(executor.getTasks());
        for (String name : taskNames) {
            if (executor.getTask(name).isPublic()) {
                System.out.println(name);
            }
        }
    }

    public static void Old() {

        // Convert all BMP in the export dir to PNG
        BmpConverter.convert();

        // Compile Tilesets
        Tilesets.compile();

        // Load sites info
        WorldSites.load();
        WorldSites.geoJson();

        // Load biome info
        BiomeInfo.load();

        // TODO: Load structures info
        // TODO: Load detailed site maps
        // TODO: Load regions info

        // Render biome tiles
        LayerRenderer satRenderer = new SatRenderer();
        satRenderer.work();

        // TODO: Render region labels
        // TODO: Place region labels
        // TODO: Place site labels
        // TODO: Place detailed site maps

        // Compile template files
        TemplateRenderer.compileUristJs();
        TemplateRenderer.compileIndexHtml();

        // Assemble output resources
        FileCopier.distResources();
    }


    private static void initFileInfo() {
        files = new FileWatcher();
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
        kryo.register(FileInfo.class);
        kryo.register(WorldInfo.class);
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


