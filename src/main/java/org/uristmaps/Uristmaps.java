package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.ini4j.Wini;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.renderer.LayerRenderer;
import org.uristmaps.renderer.SatRenderer;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by schacht on 26.05.15.
 */
public class Uristmaps {

    public static Wini settings;

    public static void main(String[] args) {
        // Load configuration file
        loadConfig();

        // TODO: Compile Tilesets
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
     * Load the config ini-style file.
     */
    private static void loadConfig() {
        // TODO: Read config file path from ARGS if provided.
        File targetFile = new File("config.cfg");
        try {
            settings = new Wini(targetFile);
        } catch (IOException e) {
            if (!targetFile.exists()){
                Log.error("Could not find the config file: " + targetFile);
            }
            System.exit(1);
        }
    }
}
