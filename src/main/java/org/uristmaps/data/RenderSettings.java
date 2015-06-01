package org.uristmaps.data;

import org.uristmaps.Uristmaps;

/**
 * Created by schacht on 26.05.15.
 */
public class RenderSettings {

    private final long scaledWorldSize;
    private final int graphicTilesPerBlock;

    private int level;

    /**
     * The offset is the first zoom level at which the world fits into
     * the result rendered map at 1px per world unit.
     */
    private int zoomOffset;

    private int stepSize;

    private int graphicsSize;
    private int clearTiles;

    public RenderSettings(int level) {
        this.level = level;

        initZoomOffset(WorldInfo.getSize());

        graphicsSize = (int) Math.pow(2,(level - zoomOffset));

        initStepSize();
        scaledWorldSize = WorldInfo.getSize() / stepSize;

        // Calculate how many tiles are clear left&top of the render to center the world on the map.
        clearTiles = 256 * (int)(Math.pow(2, zoomOffset)) - WorldInfo.getSize();
        clearTiles /= stepSize;
        clearTiles /= 2;

        graphicTilesPerBlock = 256 / getGraphicsSize();
    }



    /**
     * Calculate the step size for rendering the world at the provided level.
     */
    private void initStepSize() {
        stepSize = 1;

        /*
         When the word does not fit into the renderer at 1px steps,
         double the step size until it does.
          */
        if (graphicsSize == 0) {
            stepSize = (int) Math.pow(2, zoomOffset - level);
            graphicsSize = 1;
        }
    }

    /**
     * Calculate the zoomOffset field.
     * @param worldSize
     */
    private void initZoomOffset(long worldSize) {
        int mapsize = 256;
        this.zoomOffset = 0;
        while (mapsize < worldSize) {
            mapsize *= 2;
            this.zoomOffset += 1;
        }
    }

    public int getStepSize() {
        return stepSize;
    }

    public int getLevel() {
        return level;
    }

    public int getZoomOffset() {
        return zoomOffset;
    }

    public int getGraphicsSize() {
        return graphicsSize;
    }

    public int getClearTiles() {
        return clearTiles;
    }

    public long getScaledWorldSize() {
        return scaledWorldSize;
    }

    public int getGraphicTilesPerBlock() {
        return graphicTilesPerBlock;
    }
}
