package org.uristmaps.renderer;

import org.uristmaps.RenderSettings;
import org.uristmaps.WorldInfo;

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
     * @param level The zoom level that is currently rendered.
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
    public abstract void renderWorldTile(int x, int y);

}
