package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uristmaps.data.Site;
import org.uristmaps.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.HashMap;
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
        context.put("version", "0.3");

        Template uristJs = Velocity.getTemplate("templates/js/urist.js.vm");

        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                "js", "urist.js").toFile();
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
        context.put("sites", groupSites());
        context.put("conf", Uristmaps.conf);
        context.put("worldInfo", Uristmaps.worldInfo);
        context.put("biomeLegend", getBiomeLegend());
        context.put("version", "0.3");

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
                    || biomeName.startsWith("river") || biomeName.startsWith("wall")) {
                Log.debug("TemplateRenderer", "Skipping " + biomeName + " in biome legend.");
                continue;
            }

            // Add icon under the biome name to the result map.
            result.put(biomeName, "biome_legend/" + tileFile.getName().replace(" ", "_"));
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
        for (Site site : WorldSites.sites.values()) {
            if (!result.containsKey(site.getType())) {
                result.put(site.getType(), new TreeMap<>());
            }
            result.get(site.getType()).put(site.getName(), site);
        }

        return result;
    }

}
