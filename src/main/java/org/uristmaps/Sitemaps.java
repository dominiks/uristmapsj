package org.uristmaps;

import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.apache.commons.io.FileUtils;
import org.uristmaps.data.SitemapInfo;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;
import org.uristmaps.util.OutputFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCME
 */
public class Sitemaps {

    /**
     * DOCME
     */
    private static Pattern idFind = Pattern.compile("(site_map-(\\d+))");


    /**
     * DOCME
     */
    public static void load() {
        Map<Integer, SitemapInfo> result = new HashMap<>();
        for (File imageFile : ExportFiles.getAllSitemaps()) {
            // Resolve id of the site
            Matcher matcher = idFind.matcher(imageFile.getName());
            if (!matcher.find()) continue;
            int id = Integer.parseInt(matcher.group(2));

            // Load image
            BufferedImage img = null;
            try {
                img = ImageIO.read(imageFile);
            } catch (IOException e) {
                Log.error("SiteMaps", "Could not read image file: " + imageFile);
                if (Log.DEBUG) Log.debug("SiteMaps", "Exception", e);
                System.exit(1);
            }
            if (img == null) System.exit(1);

            // Add info to index
            SitemapInfo info = new SitemapInfo(id, img.getWidth(), img.getHeight());
            result.put(id, info);
        }

        // Save map to dist
        try (Output output = new Output(new FileOutputStream(BuildFiles.getSitemapsIndex()))) {
            Uristmaps.kryo.writeObject(output, result);
        } catch (FileNotFoundException e) {
            Log.error("SiteMaps", "Could not write sitemaps index: " + BuildFiles.getSitemapsIndex());
            if (Log.DEBUG) Log.debug("SiteMaps", "Exception", e);
            System.exit(1);
        }
    }

    /**
     * DOCME
     */
    public static void copy() {
        for (File imageFile : ExportFiles.getAllSitemaps()) {
            // Resolve id of the site
            Matcher matcher = idFind.matcher(imageFile.getName());
            if (!matcher.find()) continue;
            int id = Integer.parseInt(matcher.group(2));
            // Copy the sitemap file into the output directory
            try {
                FileUtils.copyFile(imageFile, OutputFiles.getSiteMap(id));
            } catch (IOException e) {
                Log.error("SiteMaps", "Could not copy image file to: " + OutputFiles.getSiteMap(id));
                if (Log.DEBUG) Log.debug("SiteMaps", "Exception", e);
                System.exit(1);
            }
        }
    }
}
