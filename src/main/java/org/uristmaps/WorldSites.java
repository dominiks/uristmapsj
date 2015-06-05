package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uristmaps.data.*;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFilesFinder;
import org.uristmaps.util.OutputFiles;
import org.uristmaps.util.Util;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * World sites manager.
 */
public class WorldSites {

    private static final Pattern idReader = Pattern.compile("(\\d+):");

    private static Map<Integer, Site> sites;
    private static int offset;
    private static boolean xyInitialized;
    private static int mapSize;

    public static void load() {
        Log.info("Sites", "Loading site information");

        sites = new HashMap<>();

        loadPopulationInfo();
        loadLegendsXML();

        UpdateLatLon();

        Log.debug("Sites", "Writing site info");
        File targetFile = BuildFiles.getSitesFile();
        try (Output output = new Output(new FileOutputStream(targetFile))) {
            Uristmaps.kryo.writeObject(output, sites);
        } catch (Exception e) {
            Log.warn("Sites", "Could not write sites index file: " + targetFile);
            if (Log.DEBUG) Log.debug("Exception: ", e);
        }

    }

    /**
     * Calculate the latitude & longitude for every site and update the values.
     */
    public static void UpdateLatLon() {
        for (Site site : sites.values()) {
            Coord2d latlon = xy2LonLat(site.getCoords().X(), site.getCoords().Y());
            site.setLat(latlon.X());
            site.setLon(latlon.Y());
        }
    }

    /**
     *
     * @return True if work was done
     */
    private static void loadLegendsXML() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try (Reader reader = new InputStreamReader(new FileInputStream(ExportFilesFinder.getLegendsXML()), "UTF-8")){
            InputSource source = new InputSource(reader);
            source.setEncoding("UTF-8");
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(new LegendsSiteReader(sites));
            xmlReader.parse(source);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert the x,y coordinates to a lat&lon pair for the web-map.
     * @param x
     * @param y
     * @return
     */
    private static Coord2d xy2LonLat(int x, int y) {
        if (!xyInitialized) {
            initLonLat();
        }
        x += offset;
        y += offset;
        double lonDeg = (double)x / mapSize * 360.0f - 180f;

        double n = Math.PI - (2.0 * Math.PI * y) / mapSize;
        double latDeg=  Math.toDegrees(Math.atan(Math.sinh(n)));
        return new Coord2d(lonDeg, latDeg);
    }

    private static void loadPopulationInfo() {
        // The last read site
        Site lastSite = null;

        // The matcher object for the regex
        Matcher match;

        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ExportFilesFinder.getPopulationFile()))) {

            boolean parsingSites = false; // Is active when we have reached the correct section in the file
            while ((line = reader.readLine()) != null) {
                line = line.trim(); // Remove whitespace
                if (line.length() == 0) continue;

                // Stop reading when we reach the section about outdoor population
                // TODO: Read this for some world info.
                if (line.startsWith("Outdoor Animal Populations")) break;

                if (!parsingSites && line.startsWith("Sites")) {
                    parsingSites = true;
                } else if (parsingSites) {
                    match = idReader.matcher(line);
                    // See if this is the first line of a new site
                    if (match.find()) {
                        lastSite = new Site(Integer.parseInt(match.group(0).replace(":", "")));
                        lastSite.addInfo(line);
                        sites.put(lastSite.getId(), lastSite);

                        // Parse this as info about the last created site
                    } else if (lastSite != null) {
                        lastSite.addInfo(line);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("Sites", "Could not read world population file.");
            if (Log.DEBUG) {
                Log.debug("Sites", "Last line: \"" + line + "\"");
                Log.debug("Sites", "Exception: ", e);
            }
            System.exit(1);
        }
    }

    /**
     * Write the sitesgeo.json for the output. Also contains the translated coordinates for all sites.
     */
    public static void geoJson() {
        // Apply site centers
        for (Map.Entry<Integer, Coord2> entry : SiteCenters.getCenters().entrySet()) {
            Site site = getSites().get(entry.getKey());
            site.setCoords(entry.getValue());
            Coord2d latlon = xy2LonLat(site.getCoords().X(), site.getCoords().Y());
            site.setLat(latlon.X());
            site.setLon(latlon.Y());
        }

        Map<Integer, SitemapInfo> sitemaps = loadSitemaps();

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> result = new HashMap<>();
        result.put("type", "FeatureCollection");
        List<Map> features = new LinkedList<>();
        result.put("features", features);

        // Template for popup content
        Template popupTempl = Velocity.getTemplate("templates/_site_tooltip.html.vm");
        VelocityContext context;
        StringWriter writer;

        for (Site site : sites.values()) {
            writer = new StringWriter();
            context = new VelocityContext();
            Map<String, Object> siteMap = new HashMap<>();
            features.add(siteMap);
            siteMap.put("type", "Feature");

            Map<String, Object> props = new HashMap<>();
            siteMap.put("properties", props);
            props.put("name", site.getName());
            props.put("type", site.getType());
            props.put("id", site.getId());
            props.put("img", String.format("/icons/%s.png", site.getType().replace(" ", "_")));

            context.put("site", site);
            context.put("sitemap", sitemaps.get(site.getId()));
            popupTempl.merge(context, writer);
            props.put("popupContent", writer.toString());

            Map<String, Object> geometry = new HashMap<>();
            siteMap.put("geometry", geometry);
            geometry.put("type", "Point");
            geometry.put("coordinates", new double[] {site.getLat(), site.getLon()});
        }

        File targetFile = OutputFiles.getSitesGeojson();
        targetFile.getParentFile().mkdirs();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, result);
        } catch (IOException e) {
            Log.warn("WorldSites", "Could not write js file: " + targetFile);
            if (Log.DEBUG) Log.debug("TemplateRenderer", "Exception", e);
        }
    }

    /**
     * DOCME
     * @return
     */
    private static Map<Integer, SitemapInfo> loadSitemaps() {
        try (Input input = new Input(new FileInputStream(BuildFiles.getSitemapsIndex()))) {
            return Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (FileNotFoundException e) {
            Log.error("WorldSites", "Could not read sitemaps index.");
            if (Log.DEBUG) Log.debug("WorldSites", "Exception", e);
            System.exit(1);
        }
        return null;
    }

    /**
     * DOCME
     * @return
     */
    public static Map<Integer, Site> getSites() {
        if (sites == null) initSites();
        return sites;
    }

    private static void initSites() {
        File sitesFile = BuildFiles.getSitesFile();
        try (Input input = new Input(new FileInputStream(sitesFile))) {
            sites = Uristmaps.kryo.readObject(input, HashMap.class);
            return;
        } catch (FileNotFoundException e) {
            Log.warn("Sites", "Error when reading state file: " + sitesFile);
            if (Log.DEBUG) Log.debug("Sites", "Exception", e);
        }
    }

    /**
     * Initialize the calculation vars needed to convert site coordinates into lon&lat coords.
     */
    private static void initLonLat() {
        long worldSize = WorldInfo.getSize();
        int zoom = 0;
        while (Math.pow(2, zoom) < worldSize) {
            zoom++;
        }
        mapSize = (int) Math.pow(2, zoom);
        offset = (int) ((mapSize - worldSize) / 2);
        xyInitialized = true;
    }

}
