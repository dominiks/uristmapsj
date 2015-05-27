package org.uristmaps.renderer;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.Coord2Mutable;
import org.uristmaps.data.RenderSettings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Base class for all layer renderer.
 * This
 */
public abstract class LayerRenderer {

    /**
     * Initialize the renderer for the next render call.
     */
    public LayerRenderer() {
    }

    /**
     * Render the x,y tile of the result map.
     * @param x
     * @param y
     */
    public void renderMapTile(int x, int y, RenderSettings renderSettings) {
        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();

        Coord2Mutable world = new Coord2Mutable();
        Coord2Mutable tile = new Coord2Mutable();
        // Iterate over all tiles that will be rendered.
        for (int tileX = 0; tileX < renderSettings.getGraphicTilesPerBlock(); tileX++) {
            tile.setX(tileX);
            int globalTileX = tileX + x * renderSettings.getGraphicTilesPerBlock();

            // Skip the tile if out of world bounds
            if (globalTileX < renderSettings.getClearTiles()) {
                continue;
            } else if (globalTileX >= renderSettings.getClearTiles() + renderSettings.getScaledWorldSize()) {
                break;
            }

            for (int tileY = 0; tileY < renderSettings.getGraphicTilesPerBlock(); tileY++) {
                tile.setY(tileY);
                int globalTileY = tileY + y * renderSettings.getGraphicTilesPerBlock();

                // Skip the tile if out of world bounds
                if (globalTileY < renderSettings.getClearTiles()) {
                    continue;
                } else if (globalTileY >= renderSettings.getClearTiles() + renderSettings.getScaledWorldSize()) {
                    break;
                }

                world.setX(globalTileX - renderSettings.getClearTiles() * renderSettings.getStepSize());
                world.setY(globalTileY - renderSettings.getClearTiles() * renderSettings.getStepSize());

                renderTile(world, tile, graphics, renderSettings);
            }
        }

        // Save the image to output folder.
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                Integer.toString(renderSettings.getLevel()),
                Integer.toString(x),
                y + ".png").toFile();
        targetFile.getParentFile().mkdirs();

        try {
            ImageIO.write(result, "PNG", targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render the tile within the result map-tile.
     * @param world The unit-coordinates for the point in the world that is being rendered.
     * @param tile The coordinate of this rendering graphic within the map-tile.
     * @param graphics The graphics object to render on.
     * @param renderSettings The rendersettings for this rendering process.
     */
    protected abstract void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings);

    /**
     * Have this renderer create the output tiles for all required zoom levels.
     */
    public void work() {
        // Read min&max level from config
        int minLevel = Uristmaps.conf.get("Map", "min_zoom", Integer.class);
        int maxLevel = Uristmaps.conf.get("Map", "max_zoom", Integer.class);

        // Iterate over all levels that are to be rendered
        for (int level = minLevel; level <= maxLevel; level++) {
            Log.info(getName(), "Rendering zoom level " + level);
            RenderSettings renderSettings = new RenderSettings(level);

            // Iterate over all tiles of this renderlevel and render them.
            for (int x = 0; x < Math.pow(2, level); x++) {
                for (int y = 0; y < Math.pow(2, level); y++) {
                    renderMapTile(x, y, renderSettings);
                }
            }
        }
    }

    /**
     * The name of this renderer. Used in logging output.
     * @return
     */
    public abstract String getName();
}
