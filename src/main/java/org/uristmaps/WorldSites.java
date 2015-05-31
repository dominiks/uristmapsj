package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uristmaps.data.Coord2d;
import org.uristmaps.data.Site;
import org.uristmaps.util.FileFinder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class WorldSites {

    private static final Pattern idReader = Pattern.compile("(\\d+):");

    public static Map<Integer, Site> sites;
    private static int offset;
    private static boolean xyInitialized;
    private static int mapSize;

    public static void load() {
        Log.info("Sites", "Loading site information");

        sites = new HashMap<>();

        // Read kryo world info.
        File sitesFile = Paths.get(Uristmaps.conf.fetch("Paths", "build"),
                "sites.kryo").toFile();
        if (sitesFile.exists()) {
            try (Input input = new Input(new FileInputStream(sitesFile))) {
                sites = Uristmaps.kryo.readObject(input, HashMap.class);
                return;
            } catch (FileNotFoundException e) {
                Log.warn("Sites", "Error when reading state file: " + sitesFile);
                if (Log.DEBUG) Log.debug("Sites", "Exception", e);
            }
        }

        boolean pops = loadPopulationInfo();
        boolean xml = loadLegendsXML();

        UpdateLatLon();

        // When neither did work, there's no need to write.
        if (!xml && !pops) {
            return;
        }


        Log.debug("Sites", "Writing site info");
        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "build"),
                "sites.kryo").toFile();
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
    private static void UpdateLatLon() {
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
    private static boolean loadLegendsXML() {
        File legendsFile = FileFinder.getLegendsXML();
        if (legendsFile == null) {
            System.exit(1);
        }

        // Check if work needs to be done
        File sitesFile = Paths.get(Uristmaps.conf.fetch("Paths", "build"),
                "sites.kryo").toFile();
        if (Uristmaps.files.fileOk(legendsFile) && sitesFile.exists()) {
            Log.debug("Sites", "Skipping XML reading.");
            return false;
        }

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try (Reader reader = new InputStreamReader(new FileInputStream(legendsFile), "UTF-8")){
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

        Uristmaps.files.updateFile(legendsFile);
        return true;
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

    private static boolean loadPopulationInfo() {
        // Read region_name*-world_sites_and_pops.txt
        File popFile = FileFinder.getPopulationFile();
        if (popFile == null) {
            System.exit(1);
        }

        File sitesFile = Paths.get(Uristmaps.conf.fetch("Paths", "build"),
                "sites.kryo").toFile();
        if (Uristmaps.files.fileOk(popFile) && sitesFile.exists()) {
            Log.debug("Sites", "Skipping population reading.");
            return false;
        }

        // The last read site
        Site lastSite = null;

        // The matcher object for the regex
        Matcher match;

        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(popFile))) {

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
                Log.debug("Exception: ", e);
            }
            System.exit(1);
        }

        Uristmaps.files.updateFile(popFile);

        return true;
    }

    /**
     * Write the sitesgeo.json for the output. Also contains the translated coordinates for all sites.
     */
    public static void geoJson() {
        VelocityContext context = new VelocityContext();
        context.put("sites", sites.values());

        Template uristJs = Velocity.getTemplate("templates/js/sitesgeo.js.vm");

        File targetFile = Paths.get(Uristmaps.conf.fetch("Paths", "output"),
                "js", "sitesgeo.json").toFile();
        targetFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(targetFile)) {
            uristJs.merge(context, writer);
        } catch (IOException e) {
            Log.warn("WorldSites", "Could not write js file: " + targetFile);
            if (Log.DEBUG) Log.debug("TemplateRenderer", "Exception", e);
        }

    }

    /**
     * Initialize the calculation vars needed to convert site coordinates into lon&lat coords.
     */
    private static void initLonLat() {
        long worldSize = Uristmaps.worldInfo.getSize();
        int zoom = 0;
        while (Math.pow(2, zoom) < worldSize) {
            zoom++;
        }
        mapSize = (int) Math.pow(2, zoom);
        offset = (int) ((mapSize - worldSize) / 2);
        xyInitialized = true;
    }
}
