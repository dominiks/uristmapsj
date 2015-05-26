package org.uristmaps;

import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.renderer.LayerRenderer;
import org.uristmaps.renderer.SatRenderer;

import java.util.Properties;

/**
 * Created by schacht on 26.05.15.
 */
public class Uristmaps {

    public static Properties settings;

    public static void main(String[] args) {
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


        settings = new Properties();
        settings.setProperty(Settings.OUTPUT_DIR, "output");

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
}
