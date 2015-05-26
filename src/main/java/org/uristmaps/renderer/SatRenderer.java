package org.uristmaps.renderer;

import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;

import java.awt.*;

/**
 * Renders an image from a tileset onto the map-tile.
 */
public class SatRenderer extends LayerRenderer {

    public SatRenderer(RenderSettings renderSettings, WorldInfo worldInfo) {
        super(renderSettings, worldInfo);

        // TODO: Load tileset used for rendering.
        // TODO: Get biome information
    }

    @Override
    protected void renderTile(int worldX, int worldY, int tileX, int tileY, Graphics2D graphics) {
        int imageX = tileX * renderSettings.getGraphicsSize();
        int imageY = tileY * renderSettings.getGraphicsSize();

        // TODO: Render the tile onto the graphic object at imageX,imageY position.
    }
}
