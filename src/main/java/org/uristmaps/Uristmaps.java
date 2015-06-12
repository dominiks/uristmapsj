package org.uristmaps;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import org.ini4j.Wini;
import org.uristmaps.data.*;
import org.uristmaps.tasks.*;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;
import org.uristmaps.util.FileWatcher;
import org.uristmaps.util.OutputFiles;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static org.uristmaps.util.Util.ANSI_RED;
import static org.uristmaps.util.Util.ANSI_RESET;

/**
 * Entry point for uristmaps application
 */
public class Uristmaps {

    public static final String VERSION = "0.3-SNAPSHOT";

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
        Log.info("Uristmaps " + VERSION);
        loadConfig(args);

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

        // No file management for this task until subtasks are possible.
        executor.addTaskGroup(new TilesetsTaskGroup());
        executor.addTask("BmpConvertTask", () -> BmpConverter.convert());

        executor.addTask("SitesGeojson",
                new File[]{BuildFiles.getSitesFile(),
                           BuildFiles.getWorldFile(),
                           BuildFiles.getSitemapsIndex()},
                OutputFiles.getSitesGeojson(),
                () -> WorldSites.geoJson());

        executor.addTask("Sites",
                new File[]{ExportFiles.getLegendsXML(),
                           ExportFiles.getPopulationFile(),
                           BuildFiles.getStructureGroups(),
                           BuildFiles.getStructureGroupsDefinitions()},
                BuildFiles.getSitesFile(),
                () -> WorldSites.load());

        executor.addTask("CompileUristJs",
                OutputFiles.getPopulationJs(),
                OutputFiles.getUristJs(),
                () -> TemplateRenderer.compileUristJs());

        executor.addTask(new CompileIndexTask());

        executor.addTask("DistResources", () -> FileCopier.distResources());

        executor.addTask("PopulationHeatmaps",
                new File[0],
                OutputFiles.getPopulationJs(),
                () -> Heatmaps.writePopulationsJS());

        executor.addTask("WorldInfo",
                new File[] {ExportFiles.getWorldHistory(),
                            ExportFiles.getBiomeMap()},
                BuildFiles.getWorldFile(),
                () -> WorldInfo.load());

        executor.addTask("BiomeInfoTask",
                ExportFiles.getBiomeMap(),
                BuildFiles.getBiomeInfo(),
                () -> BiomeInfo.load());

        executor.addTaskGroup(new SatRendererTaskGroup());

        List<File> dependantForSitemaps = new LinkedList<>();
        dependantForSitemaps.add(BuildFiles.getSitemapsIndex());
        dependantForSitemaps.addAll(Arrays.asList(OutputFiles.getAllSiteMaps()));
        executor.addTask("LoadSitemaps",
                ExportFiles.getAllSitemaps(),
                dependantForSitemaps.toArray(new File[0]),
                () -> Sitemaps.load());

        executor.addTask("CopySiteMaps",
                ExportFiles.getAllSitemaps(),
                OutputFiles.getAllSiteMaps(),
                () -> Sitemaps.copy());

        executor.addTask("GroupStructures",
                new File[] {ExportFiles.getStructuresMap(),
                            BuildFiles.getWorldFile()},
                new File[]{BuildFiles.getStructureGroups(),
                        BuildFiles.getStructureGroupsDefinitions()},
                () -> StructureGroups.load());

        executor.addTask(new FullBuildMetaTask());

        executor.addTask("Host", () -> WebServer.start());

        // Parse more parameters
        for (String arg : args) {
            if (arg.equalsIgnoreCase("tasks") ||arg.equalsIgnoreCase("list")) {
                echoTasks(executor);
                return;
            } else if (arg.equalsIgnoreCase("forget")) {
                forgetInputFiles();
                return;
            } else if (arg.equalsIgnoreCase("help")) {
                usage();
                return;
            }
        }

        // Check if we can stop initializing here and start the web server.
        for (String arg : args) {
            if (arg.equalsIgnoreCase("host")) {
                executor.exec("Host");
                return;
            }
        }

        // Run the default task or the requested task.
        Log.info("Starting full build");
        executor.exec("BmpConvertTask", "FullBuild");
    }

    /**
     * Print some help text.
     */
    private static void usage() {
        System.out.println("Usage: ");
        System.out.println("\tlist\tTo list all available tasks.");
        System.out.println("\tforget\tTo forget file states and not skip any tasks.");
        System.out.println("\thost\tTo start the local webserver.");
    }

    /**
     * Remove the file state store so all tasks will run as if its their first time, in the next run.
     */
    private static void forgetInputFiles() {
        System.out.println("Forgetting all file states.");
        files.forget();
    }

    /**
     * Print all public tasks.
     * @param executor
     */
    private static void echoTasks(TaskExecutor executor) {
        // List all available tasks
        System.out.println("Available tasks:");
        TreeSet<String> taskNames = new TreeSet<>(executor.getTasks());
        taskNames.stream().filter(name -> executor.getTask(name).isPublic()).forEach(System.out::println);
    }

    public static void Old() {
        // TODO: Load regions info

        // TODO: Render region labels
        // TODO: Place region labels
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
        kryo.register(StructureGroup.class);
        kryo.register(SitemapInfo.class);
    }

    /**
     * Load the config ini-style file.
     */
    private static void loadConfig(String[] args) {
        File targetFile = new File("config.cfg"); // Default config location

        // Read config file path from ARGS if provided
        for (int i= 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                if (i + 1 < args.length) {
                    targetFile = new File(args[i+1]);
                    break;
                }
            }
        }

        try {
            conf = new Wini(targetFile);
        } catch (IOException e) {
            if (!targetFile.exists()){
                Log.error("Could not find the config file: " + targetFile);
            }
            System.exit(1);
        }

        // Validate config
        List<String> missing = new LinkedList<>();
        validateEntry("Paths", "export", missing);
        validateEntry("Paths", "region_name", missing);
        validateEntry("Paths", "build", missing);
        validateEntry("Paths", "output", missing);
        validateEntry("Paths", "tiles", missing);
        validateEntry("Paths", "tilesets", missing);
        validateEntry("App", "processes", missing);
        validateEntry("App", "debug", missing);
        validateEntry("App", "log_blacklist", missing);
        validateEntry("Map", "max_zoom", missing);
        validateEntry("Map", "min_zoom", missing);
        validateEntry("Map", "max_cluster_radius", missing);
        validateEntry("Map", "show_spoilers", missing);
        validateEntry("Map", "map_font", missing);
        validateEntry("Output", "footer", missing);
        validateEntry("Web", "port", missing);

        if (!missing.isEmpty()) {
            for (String msg : missing) {
                Log.error(String.format(ANSI_RED + "Entry missing in config file: %s" + ANSI_RESET, msg));
            }
            System.exit(1);
        }
    }

    /**
     * Make sure the provided entry is in the given category of the config. If not, add
     * an entry to the list.
     * @param category
     * @param entry
     * @param missing
     */
    private static void validateEntry(String category, String entry, List<String> missing) {
        if (conf.get(category, entry) == null) {
            missing.add(String.format("%s->%s", category, entry));
        }
    }

    /**
     * A logger that can ignore items belonging to a set category.
     */
    static public class FilteringLogger extends Log.Logger {

        private final Set<String> blacklist;

        private long startUpTime = new Date().getTime();

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
            logMsg(level, category, message, ex);
        }

        private void logMsg(int level, String category, String message, Throwable ex) {
            StringBuilder builder = new StringBuilder(256);

            long time = new Date().getTime() - startUpTime;
            long minutes = time / (1000 * 60);
            long seconds = time / (1000) % 60;
            if (minutes <= 9) builder.append('0');
            builder.append(minutes);
            builder.append(':');
            if (seconds <= 9) builder.append('0');
            builder.append(seconds);

            switch (level) {
                case Log.LEVEL_ERROR:
                    //builder.append(Util.ANSI_RED);
                    builder.append(" ERROR: ");
                    break;
                case Log.LEVEL_WARN:
                    //builder.append(Util.ANSI_YELLOW);
                    builder.append("  WARN: ");
                    break;
                case Log.LEVEL_INFO:
                    builder.append("  INFO: ");
                    break;
                case Log.LEVEL_DEBUG:
                    //builder.append(Util.ANSI_GREEN);
                    builder.append(" DEBUG: ");
                    break;
                case Log.LEVEL_TRACE:
                    builder.append(" TRACE: ");
                    break;
            }

            if (category != null) {
                builder.append('[').append(category).append("] ");
            }

            builder.append(message);

            if (ex != null) {
                StringWriter writer = new StringWriter(256);
                ex.printStackTrace(new PrintWriter(writer));
                builder.append('\n');
                builder.append(writer.toString().trim());
            }

            //builder.append(Util.ANSI_RESET);
            print(builder.toString());
        }
    }
}


