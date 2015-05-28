package org.uristmaps.renderer;

import org.uristmaps.BiomeInfo;
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

    public SatRenderer() {
        super();


        // TODO: Load tileset used for rendering.

        // Assert biome information is available
        assert BiomeInfo.biomeInfo != null: "Biome data missing!";
    }

    @Override
    protected void prepareForLevel(int level, RenderSettings renderSettings) {
        tilesIndex = Tilesets.getTilesetIndex(renderSettings.getGraphicsSize());
        tilesImage = Tilesets.getTilesetImage(renderSettings.getGraphicsSize());
    }

    @Override
    protected void renderTile(Coord2 world, Coord2 tile, Graphics2D graphics, RenderSettings renderSettings) {
        Coord2 imageCoords = new Coord2(tile.X() * renderSettings.getGraphicsSize(),
                                        tile.Y() * renderSettings.getGraphicsSize());

        // Render the tile onto the graphic object at imageX,imageY position.
        String biomeName = BiomeInfo.biomeInfo[world.X()][world.Y()];
        assert biomeName != null: "No biome name found in biome data!";
        Coord2 tileCoord = tilesIndex.get(biomeName);
        assert tileCoord != null: "No tile found for biome: " + biomeName;
        assert tileCoord.X() < tilesImage.getWidth(): "Tile X is out of bounds!";
        assert tileCoord.Y() < tilesImage.getHeight(): "Tile Y is out of bounds!";

        BufferedImage tileImage = tilesImage.getSubimage(tileCoord.X(), tileCoord.Y(),
                renderSettings.getGraphicsSize(), renderSettings.getGraphicsSize());
        graphics.drawImage(tileImage, imageCoords.X(), imageCoords.Y(), null);

    }

    @Override
    public String getName() {
        return "SatRenderer";
    }
}
