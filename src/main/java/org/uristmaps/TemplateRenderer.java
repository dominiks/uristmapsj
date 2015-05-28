package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;

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
        context.put("map", Uristmaps.conf.get("Map"));
        context.put("version", "0.3");

        Template uristJs = Velocity.getTemplate("templates/js/urist.js");

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
        context.put("map", Uristmaps.conf.get("Map"));
        context.put("version", "0.3");

        Template uristJs = Velocity.getTemplate("templates/index.html");

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

}
