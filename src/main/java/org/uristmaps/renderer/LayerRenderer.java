package org.uristmaps.renderer;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.Uristmaps;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.Coord2Mutable;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.util.Progress;
import org.uristmaps.util.StepProgress;
import org.uristmaps.util.UnitProgress;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base class for all layer renderer.
 */
public abstract class LayerRenderer {

    /**
     * The zoom level for this render instance.
     */
    private final int level;

    /**
     * Create a layer renderer for a given zoom level.
     */
    public LayerRenderer(int level) {
        this.level = level;
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

                world.setX((globalTileX - renderSettings.getClearTiles()) * renderSettings.getStepSize());
                world.setY((globalTileY - renderSettings.getClearTiles()) * renderSettings.getStepSize());

                renderTile(world, tile, graphics, renderSettings);
            }
        }

        // Save the image to output folder.
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                getFolderName(),
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
     * This is the level that will be drawn in the next batch. Load resources for this zoom level.
     * @param level
     */
    protected abstract void prepareForLevel(int level, RenderSettings renderSettings);

    /**
     * Render the tile within the result map-tile.
     * @param world The unit-coordinates for the point in the world that is being rendered.
     * @param tile The coordinate of this rendering graphic within the map-tile.
     * @param graphics The graphics object to render on.
     * @param renderSettings The rendersettings for this rendering process.
     */
    protected abstract void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings);

    /**
     * The name of the folder within the output directory where the tiles will be placed in.
     * @return
     */
    protected abstract String getFolderName();

    /**
     * Have this renderer create the output tiles for all required zoom levels.
     */
    public void work() {
        // Iterate over all levels that are to be rendered
        RenderSettings renderSettings = new RenderSettings(level);
        prepareForLevel(level, renderSettings);

        Log.info(getName(), String.format("Rendering zoom level %d using %dpx sized graphics (%d cols).",
                level,  renderSettings.getGraphicsSize(), (int)Math.pow(2, level)));

        //Progress prog = new UnitProgress((int) Math.pow(2, level), 1, getName());
        Progress prog = new StepProgress((int) Math.pow(2, level), 1, getName());

        int poolsize = Uristmaps.conf.get("App", "processes", Integer.class);

        Log.debug(getName(), String.format("Creating thread pool with size %d", poolsize));
        ExecutorService pool = Executors.newFixedThreadPool(poolsize);

        List<Callable<Object>> columnRenderTasks = new LinkedList<>();
        // Iterate over all tiles of this renderlevel and render them.
        // To make this multithreaded, make each "column" a separate job, so one
        // job per x-coordinate. These jobs will be processed by a threaded pool.
        for (int x = 0; x < Math.pow(2, level); x++) {
            final int finalX = x;

            columnRenderTasks.add(() -> {
                for (int y = 0; y < Math.pow(2, level); y++) {
                    renderMapTile(finalX, y, renderSettings);
                }
                prog.show();
                return null;
            });
        }
        Log.debug(getName(), "Waiting for pool to shutdown.");
        try {
            pool.invokeAll(columnRenderTasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            pool.shutdown();
        }
    }

    /**
     * The name of this renderer. Used in logging output.
     * @return
     */
    public abstract String getName();
}
