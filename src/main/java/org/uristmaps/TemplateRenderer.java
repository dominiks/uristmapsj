package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uristmaps.data.Site;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.util.OutputFiles;
import sun.swing.StringUIClientPropertyKey;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates the js files from the templates and moves them into the output folder.
 * Created by dominik on 28.05.2015.
 */
public class TemplateRenderer {

    /**
     * Load the urist.js template and compile it with project information.
     */
    public static void compileUristJs() {
        Log.info("TemplateRenderer", "Writing urist.js");
        VelocityContext context = new VelocityContext();
        context.put("conf", Uristmaps.conf);
        context.put("version",Uristmaps.VERSION);

        Template uristJs = Velocity.getTemplate("templates/js/urist.js.vm");

        File targetFile = OutputFiles.getUristJs();
        targetFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(targetFile)) {
            uristJs.merge(context, writer);
        } catch (IOException e) {
            Log.warn("TemplateRenderer", "Could not write js file: " + targetFile);
            if (Log.DEBUG) Log.debug("TemplateRenderer", "Exception", e);
        }
    }

    public static void compileIndexHtml() {
        Log.info("TemplateRenderer", "Writing index.html");
        VelocityContext context = new VelocityContext();
        Map<String, Map<String, Site>> groups = groupSites();

        context.put("sites", groups);
        context.put("conf", Uristmaps.conf);
        context.put("worldInfo", WorldInfo.class);

        Map<String, String> biomeLegend = getBiomeLegend();
        biomeLegend.put("Farmland", "biome_legend/farmland.png");
        biomeLegend.put("Road", "biome_legend/road_we.png");
        biomeLegend.put("River", "biome_legend/river_we.png");
        biomeLegend.put("Tunnel", "biome_legend/tunnel_we.png");
        context.put("biomeLegend", biomeLegend);

        context.put("version", Uristmaps.VERSION);
        context.put("populations", WorldSites.getTotalPopulation());

        // Check if there's a file for footer contents
        String path = Uristmaps.conf.fetch("Output", "footer");
        if (StringUtils.isNotEmpty(path)) {
            File footer = new File(path);
            if (footer.exists()) {
                try {
                    context.put("footer", FileUtils.readFileToString(footer));
                } catch (IOException e) {
                    Log.error("TemplateRenderer", "Could not read footer file: " + footer.getAbsolutePath());
                    if (Log.DEBUG) Log.debug("TemplateRenderer", "Exception", e);
                    System.exit(1);
                }
            } else {
                Log.error("TemplateRenderer", "Footer file does not exist: " + footer.getAbsolutePath());
            }
        } else {
            context.put("footer", "");
        }

        Template uristJs = Velocity.getTemplate("templates/index.html.vm");

        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                "index.html").toFile();
        targetFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(targetFile)) {
            uristJs.merge(context, writer);
        } catch (IOException e) {
            Log.warn("TemplateRenderer", "Could not write js file: " + targetFile);
            if (Log.DEBUG) Log.debug("TemplateRenderer", "Exception", e);
        }
    }

    /**
     * Create a map of all Biomes, mapping the biome name to the url of the biome image.
     * The map is ordered by its keys.
     * @return
     */
    private static Map<String, String> getBiomeLegend() {
        Map<String, String> result = new TreeMap<>();

        // Get list of all biomeicons in 32px folder
        File tilesDir = Paths.get(Uristmaps.conf.fetch("Paths", "tiles"), "32").toFile();
        for (File tileFile : tilesDir.listFiles(filename -> filename.getName().endsWith(".png"))) {
            String biomeName = FilenameUtils.removeExtension(tileFile.getName());
            if (biomeName.startsWith("castle") || biomeName.startsWith("village")
                    || biomeName.startsWith("river") || biomeName.startsWith("wall")
                    || biomeName.startsWith("road") || biomeName.startsWith("tunnel")
                    || biomeName.startsWith("farmland") || biomeName.startsWith("bridge")) {
                Log.trace("TemplateRenderer", "Skipping " + biomeName + " in biome legend.");
                continue;
            }

            // Add icon under the biome name to the result map.
            result.put(WordUtils.capitalize(biomeName.replace("_", " ")), "biome_legend/" + tileFile.getName().replace(" ", "_"));
        }

        return result;
    }

    /**
     * Creates a Map of all sites that provides Type -> Name -> Site to group sites by their types.
     * @return
     */
    private static Map<String, Map<String, Site>> groupSites() {
        Map<String, Map<String, Site>> result = new TreeMap<>();

        // Iterate over all sites
        for (Site site : WorldSites.getSites().values()) {
            if (!result.containsKey(site.getType())) {
                result.put(site.getType(), new TreeMap<>());
            }
            result.get(site.getType()).put(site.getName(), site);
        }

        return result;
    }

}
