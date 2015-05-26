package org.uristmaps.renderer;

import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;

import java.awt.image.BufferedImage;

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
    public void init(RenderSettings renderSettings, WorldInfo worldinfo) {
        this.renderSettings = renderSettings;
        this.worldInfo = worldinfo;
    }

    /**
     * Render the x,y tile of the result map.
     * @param x
     * @param y
     */
    public void renderWorldTile(int x, int y) {
        long scaledWorldSize = worldInfo.getSize() / renderSettings.getStepSize();

        BufferedImage result = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);




    }

}
