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
        settings = new Properties();
        settings.setProperty(Settings.OUTPUT_DIR, "output");

        WorldInfo worldInfo = new WorldInfo(2050);
        RenderSettings renderSettings = new RenderSettings(4, worldInfo);

        LayerRenderer renderer = new SatRenderer(renderSettings, worldInfo);
        renderer.renderMapTile(0,0);
    }
}
