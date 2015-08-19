package org.uristmaps.data;

/**
 *
 */
public class SitemapInfo {

    private int siteId;
    private Coord2 imageSize;

    public SitemapInfo() {}

    public SitemapInfo(int id, int width, int height) {
        this.siteId = id;
        this.imageSize = new Coord2(width, height);
    }

    public String getUrl() {
        return String.format("sitemaps/%d.png", siteId);
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Coord2 getImageSize() {
        return imageSize;
    }

    public void setImageSize(Coord2 imageSize) {
        this.imageSize = imageSize;
    }

    public int getWidth() {
        return imageSize.X();
    }

    public int getHeight() {
        return imageSize.Y();
    }
}
