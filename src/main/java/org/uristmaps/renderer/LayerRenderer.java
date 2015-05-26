package org.uristmaps.renderer;

import org.uristmaps.Settings;
import org.uristmaps.Uristmaps;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base class for all layer renderer.
 * This
 */
public abstract class LayerRenderer {
    /**
     * The zoom level that will be rendered for.
     */
    protected RenderSettings renderSettings;
    protected WorldInfo worldInfo;

    /**
     * Initialize the renderer for the next render call.
     */
    public LayerRenderer(RenderSettings renderSettings, WorldInfo worldinfo) {
        this.renderSettings = renderSettings;
        this.worldInfo = worldinfo;
    }

    /**
     * Render the x,y tile of the result map.
     * @param x
     * @param y
     */
    public void renderMapTile(int x, int y) {
        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();


        // Iterate over all tiles that will be rendered.
        for (int tileX = 0; tileX < renderSettings.getGraphicTilesPerBlock(); tileX++) {
            int globalTileX = tileX + x * renderSettings.getGraphicTilesPerBlock();

            // Skip the tile if out of world bounds
            if (globalTileX < renderSettings.getClearTiles()) {
                continue;
            } else if (globalTileX >= renderSettings.getClearTiles() + renderSettings.getScaledWorldSize()) {
                break;
            }

            for (int tileY = 0; tileY < renderSettings.getGraphicTilesPerBlock(); tileY++) {
                int globalTileY = tileY + y * renderSettings.getGraphicTilesPerBlock();

                // Skip the tile if out of world bounds
                if (globalTileY < renderSettings.getClearTiles()) {
                    continue;
                } else if (globalTileY >= renderSettings.getClearTiles() + renderSettings.getScaledWorldSize()) {
                    break;
                }

                int worldX = globalTileX - renderSettings.getClearTiles() * renderSettings.getStepSize();
                int worldY = globalTileY - renderSettings.getClearTiles() * renderSettings.getStepSize();

                renderTile(worldX, worldY, tileX, tileY, graphics);
            }
        }

        // Save the image to output folder.
        File targetFile = Paths.get(Uristmaps.settings.getProperty(Settings.OUTPUT_DIR),
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
     *
     * @param worldX
     * @param worldY
     * @param tileX
     * @param tileY
     * @param graphics
     */
    protected abstract void renderTile(int worldX, int worldY, int tileX, int tileY, Graphics2D graphics);

}
