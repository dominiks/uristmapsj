package org.uristmaps.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders an image from a tileset onto the map-tile.
 */
public class SatRenderer extends LayerRenderer {

    public SatRenderer() {
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
