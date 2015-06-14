package org.uristmaps;

import com.esotericsoftware.minlog.Log;
import org.uristmaps.data.Coord2;
import org.uristmaps.data.Site;
import org.uristmaps.util.Util;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

/**
 * SAX parser to retrieve the "coords"-element for each "site" element.
 */
public class LegendsSiteReader extends DefaultHandler {

    private Map<Integer, Site> sites;

    private boolean readingSites = false;
    private boolean readingId = false;
    private boolean readingCoords = false;

    private Site lastSite;

    public LegendsSiteReader(Map<Integer, Site> sites) {
        this.sites = sites;
    }

    @Override
    public void startDocument() throws SAXException {
        Log.debug("LegendsParser", "Starting document");
    }

    @Override
    public void endDocument() throws SAXException {
        Log.debug("LegendsParser", "Finished Document");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("sites")) {
            readingSites = true;
        } else if (readingSites) {
            if (localName.equals("id")) {
                readingId = true;
            } else if (localName.equals("coords")) {
                readingCoords = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (readingSites && localName.equals("sites")) {
            readingSites = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readingId) {
            String content = String.copyValueOf(ch, start, length);
            readingId = false;
            lastSite = sites.get(Integer.parseInt(content));
        } else if (readingCoords) {
            String content = String.copyValueOf(ch, start, length);
            readingCoords = false;
            String[] split = content.split(",");
            Coord2 parsedCoords = new Coord2(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            lastSite.setCoords(Util.worldToUnitCentered(parsedCoords));
        }
    }
}
