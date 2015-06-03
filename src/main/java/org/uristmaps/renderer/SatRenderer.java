package org.uristmaps.renderer;

import org.uristmaps.BiomeInfo;
import org.uristmaps.StructureInfo;
import org.uristmaps.Tilesets;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Renders an image from a tileset onto the map-tile.
 */
public class SatRenderer extends LayerRenderer {

    /**
     * The index for the tileset that is used.
     */
    private Map<String, Coord2> tilesIndex;

    /**
     * The tileset image used for rendering.
     */
    private BufferedImage tilesImage;

    /**
     * Biome data.
     */
    private String[][] biomeInfo;

    /**
     * Image buffer used to store the tiles that are pasted on the graphics object.
     * This is a member to prevent a new variable initialization for every single map-tile
     * that will be rendered.
     */
    private BufferedImage bufImage;

    /**
     * Top left coordinates in px where the tile image will be pasted on in the result
     * image.
     * This is a member to prevent a new variable initialization for every single map-tile
     * that will be rendered.
     */
    private Coord2 imageCoords;

    /**
     * The tile-coordinate for the tile to render within the result image.
     * This is a member to prevent a new variable initialization for every single map-tile
     * that will be rendered.
     */
    private Coord2 tileCoord;

    /**
     * Name of the biome found at the world coordinate to render.
     * This is a member to prevent a new variable initialization for every single map-tile
     * that will be rendered.
     */
    private String biomeName;

    /**
     * Name of the structure found at the world coordinate to render.
     * This is a member to prevent a new variable initialization for every single map-tile
     * that will be rendered.
     */
    private String structure;

    /**
     * Create a layer renderer for a given zoom level.
     *
     * @param level
     */
    public SatRenderer(int level) {
        super(level);
    }

    @Override
    protected void prepareForLevel(int level, RenderSettings renderSettings) {
        tilesIndex = Tilesets.getTilesetIndex(renderSettings.getGraphicsSize());
        tilesImage = Tilesets.getTilesetImage(renderSettings.getGraphicsSize());

        biomeInfo = BiomeInfo.getBiomeData();
    }

    @Override
    protected void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings) {
        imageCoords = new Coord2(tile.X() * renderSettings.getGraphicsSize(),
                                        tile.Y() * renderSettings.getGraphicsSize());

        // Render the tile onto the graphic object at imageX,imageY position.
        biomeName = biomeInfo[world.X()][world.Y()];
        assert biomeName != null;
        tileCoord = tilesIndex.get(biomeName);
        assert tileCoord != null;
        assert tileCoord.X() < tilesImage.getWidth(): "Tile X is out of bounds!";
        assert tileCoord.Y() < tilesImage.getHeight(): "Tile Y is out of bounds!";

        bufImage = tilesImage.getSubimage(tileCoord.X(), tileCoord.Y(),
                renderSettings.getGraphicsSize(), renderSettings.getGraphicsSize());
        graphics.drawImage(bufImage, imageCoords.X(), imageCoords.Y(), null);

        // Check if there is a structure at the coordinate and render that too
        structure = StructureInfo.getSuffixed(world.X(), world.Y());
        if (structure == null) return;
        tileCoord = tilesIndex.get(structure);

        // Skip this structure if no tile is available.
        if (tileCoord == null) return;
        assert tileCoord.X() < tilesImage.getWidth(): "Tile X is out of bounds!";
        assert tileCoord.Y() < tilesImage.getHeight(): "Tile Y is out of bounds!";

        bufImage = tilesImage.getSubimage(tileCoord.X(), tileCoord.Y(),
                renderSettings.getGraphicsSize(), renderSettings.getGraphicsSize());
        graphics.drawImage(bufImage, imageCoords.X(), imageCoords.Y(), null);
    }

    @Override
    protected String getFolderName() {
        return "tiles";
    }

    @Override
    public String getName() {
        return "SatRenderer";
    }
}
