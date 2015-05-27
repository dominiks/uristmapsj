package org.uristmaps.renderer;

import org.uristmaps.data.Coord2;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;

import java.awt.*;

/**
 * Renders an image from a tileset onto the map-tile.
 */
public class SatRenderer extends LayerRenderer {

    public SatRenderer() {
        super();

        // TODO: Load tileset used for rendering.

        // TODO: Get biome information
    }

    @Override
    protected void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings) {
        Coord2 imageCoords = new Coord2(tile.X() * renderSettings.getGraphicsSize(),
                                        tile.Y() * renderSettings.getGraphicsSize());

        // TODO: Render the tile onto the graphic object at imageX,imageY position.
        

    }

    @Override
    public String getName() {
        return "SatRenderer";
    }
}
