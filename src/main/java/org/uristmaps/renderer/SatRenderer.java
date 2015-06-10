package org.uristmaps.renderer;

import org.uristmaps.BiomeInfo;
import org.uristmaps.StructureInfo;
import org.uristmaps.Tilesets;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.RenderSettings;
import org.uristmaps.data.WorldInfo;
import org.uristmaps.util.BuildFiles;

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

    private String[][] suffixed;

    /**
     * Color table used when no tile image is available.
     */
    private Map<String, Color> colorTable;

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
        if (BuildFiles.getTilesetImage(renderSettings.getGraphicsSize()).exists()) {
            tilesIndex = Tilesets.getTilesetIndex(renderSettings.getGraphicsSize());
            tilesImage = Tilesets.getTilesetImage(renderSettings.getGraphicsSize());
        } else {
            colorTable = Tilesets.getColorTable(renderSettings.getGraphicsSize());
        }

        biomeInfo = BiomeInfo.getBiomeData();

        suffixed = StructureInfo.getSuffixed();
    }

    @Override
    protected void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings) {
        Coord2 imageCoords = new Coord2(tile.X() * renderSettings.getGraphicsSize(),
                tile.Y() * renderSettings.getGraphicsSize());

        // Render the tile onto the graphic object at imageX,imageY position.
        String biomeName = biomeInfo[world.X()][world.Y()];
        assert biomeName != null;

        // When the color table is set for this level, just draw the color and leave it.
        if (colorTable != null) {
            graphics.setColor(colorTable.get(biomeName));

            // try to find a color for the structure, remove the suffix from it
            String struct = suffixed[world.X()][world.Y()];
            if (struct != null) {
                if (struct.contains("_")) struct = struct.substring(0, struct.lastIndexOf("_"));
                if (colorTable.containsKey(struct)) {
                    graphics.setColor(colorTable.get(struct));
                }
            }

            graphics.fillRect(imageCoords.X(), imageCoords.Y(),
                    renderSettings.getGraphicsSize(), renderSettings.getGraphicsSize());
            return;
        }

        // Color table was null, so use the tileset to render biome and structure.

        Coord2 tileCoord = tilesIndex.get(biomeName);
        assert tileCoord != null;
        assert tileCoord.X() < tilesImage.getWidth(): "Tile X is out of bounds!";
        assert tileCoord.Y() < tilesImage.getHeight(): "Tile Y is out of bounds!";

        BufferedImage bufImage = tilesImage.getSubimage(tileCoord.X(), tileCoord.Y(),
                renderSettings.getGraphicsSize(), renderSettings.getGraphicsSize());
        graphics.drawImage(bufImage, imageCoords.X(), imageCoords.Y(), null);

        // Check if there is a structure at the coordinate and render that too
        String structure = suffixed[world.X()][world.Y()];
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
