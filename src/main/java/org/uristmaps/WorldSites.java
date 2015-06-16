package org.uristmaps;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.uristmaps.data.*;
import org.uristmaps.util.BuildFiles;
import org.uristmaps.util.ExportFiles;
import org.uristmaps.util.OutputFiles;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * World sites manager.
 */
public class WorldSites {

    /**
     * Regexp to read the site id from a line in the populations file.
     */
    private static final Pattern idReader = Pattern.compile("(\\d+):");

    /**
     * Map of all available sites. Maps them by id.
     */
    private static Map<Integer, Site> sites;

    /**
     * Offset of the world relative to the size of the rendered map. The map is bigger
     * than the world, and the offset left and above moves the world into the center of the map.
     */
    private static int offset;

    /**
     * Whether the xy translation variables have been initialized.
     */
    private static boolean xyInitialized;

    /**
     * Size of the world map.
     */
    private static int mapSize;

    /**
     * DOCME
     */
    private static TreeMap<String, List<Site>> popDistribution;

    /**
     * DOCME
     */
    private static TreeMap<String, Integer> maxPop;

    /**
     * DOCME
     */
    private static TreeMap<String, Integer> totalPop;

    /**
     * DOCME
     */
    static Map<String, String> nameTransform = new HashMap<>();

    static {
        nameTransform.put("Alligator", "Alligators");
        nameTransform.put("Alligator Outcast", "Alligators Outcasts");
        nameTransform.put("Anaconda", "Anacondas");
        nameTransform.put("Anaconda Man", "Anaconda Men");
        nameTransform.put("Animated Dwarf", "Animated Dwarves");
        nameTransform.put("Animated Elf", "Animated Elves");
        nameTransform.put("Animated Human", "Animated Humans");
        nameTransform.put("Animated Goblin", "Animated Goblins");
        nameTransform.put("Axolotl", "Axolotls");
        nameTransform.put("Bat", "Bats");
        nameTransform.put("Bat Man", "Bat Men");
        nameTransform.put("Black Bear", "Black Bears");
        nameTransform.put("Bleak Man", "Bleak Men");
        nameTransform.put("Blind Horror", "Blind Horrors");
        nameTransform.put("Blue Peafowl", "Blue Peafowls");
        nameTransform.put("Bronze Colossus", "Bronze Colossuses");
        nameTransform.put("Cat", "Cats");
        nameTransform.put("Chicken", "Chickens");
        nameTransform.put("Cougar", "Cougars");
        nameTransform.put("Creature Of Twilight", "Creatures Of Twilight");
        nameTransform.put("Cyclops", "Cyclopses");
        nameTransform.put("Dark Creature", "Dark Creatures");
        nameTransform.put("Dingo", "Dingoes");
        nameTransform.put("Dingo Man", "Dingo Men");
        nameTransform.put("Dragon", "Dragons");
        nameTransform.put("Dusk Horror", "Dusk Horrors");
        nameTransform.put("Dwarf", "Dwarves");
        nameTransform.put("Dwarf Outcast", "Dwarf Outcasts");
        nameTransform.put("Elf", "Elves");
        nameTransform.put("Elf Outcast", "Elf Outcasts");
        nameTransform.put("Elf Prisoner", "Elf Prisoners");
        nameTransform.put("Ettin", "Ettins");
        nameTransform.put("Forest Titan", "Forest Titans");
        nameTransform.put("Giant", "Giants");
        nameTransform.put("Giant Desert Scorpion", "Giant Desert Scorpions");
        nameTransform.put("Giant Dingo", "Giant Dingoes");
        nameTransform.put("Giant Jaguar", "Giant Jaguars");
        nameTransform.put("Giant Leopard", "Giant Leopards");
        nameTransform.put("Giant Tiger", "Giant Tigers");
        nameTransform.put("Goblin", "Goblins");
        nameTransform.put("Goblin Outcast", "Goblin Outcasts");
        nameTransform.put("Goblin Prisoner", "Goblin Prisoners");
        nameTransform.put("Goose", "Geese");
        nameTransform.put("Grizzly Bear", "Grizzly Bears");
        nameTransform.put("Groundhog", "Groundhogs");
        nameTransform.put("Hill Titan", "Hill Titans");
        nameTransform.put("Hamster", "Hamsters");
        nameTransform.put("Human", "Humans");
        nameTransform.put("Hydra", "Hydras");
        nameTransform.put("Hyena Man", "Hyena Men");
        nameTransform.put("Jaguar", "Jaguars");
        nameTransform.put("Jungle Titan", "Jungle Titans");
        nameTransform.put("Kobold", "Kobolds");
        nameTransform.put("Kobold Outcast", "Kobold Outcasts");
        nameTransform.put("Kobold Prisoner", "Kobold Prisoners");
        nameTransform.put("Marsh Titan", "Marsh Titans");
        nameTransform.put("Midnight Brute", "Midnight Brutes");
        nameTransform.put("Minotaur", "Minotaurs");
        nameTransform.put("Monster Of Twilight", "Monsters Of Twilight");
        nameTransform.put("Monster Of Twilight Outcast", "Monsters Of Twilight Outcasts");
        nameTransform.put("Pig", "Pigs");
        nameTransform.put("Plains Titan", "Plains Titans");
        nameTransform.put("Polar Bear", "Polar Bears");
        nameTransform.put("Roc", "Rocs");
        nameTransform.put("Sasquatch", "Sasquatches");
        nameTransform.put("Tiger Man", "Tiger Men");
        nameTransform.put("Troll", "Trolls");
        nameTransform.put("Troll Outcast", "Troll Outcasts");
        nameTransform.put("Wicked Freak", "Wicked Freaks");
        nameTransform.put("Wolf", "Wolves");
        nameTransform.put("Tundra Titan", "Tundra Titans");
        nameTransform.put("Wicked Creature", "Wicked Creatures");
        nameTransform.put("Yeti", "Yetis");
    }

    /**
     * Load the data for all sites from the export files and write them to the sites file.
     */
    public static void load() {
        Log.info("Sites", "Loading site information");

        sites = new HashMap<>();

        loadPopulationInfo();
        loadLegendsXML();

        // Set lat&lon for all sites to their current position
        for (Site site : sites.values()) {
            Coord2d latlon = xy2LonLat(site.getCoords().X(), site.getCoords().Y());
            site.setLat(latlon.X());
            site.setLon(latlon.Y());
        }

        loadSiteCenters();
        // Apply site centers
        for (Map.Entry<Integer, Coord2> entry : SiteCenters.getCenters().entrySet()) {
            Site site = getSites().get(entry.getKey());
            site.setCoords(entry.getValue());
            Coord2d latlon = xy2LonLat(site.getCoords().X(), site.getCoords().Y());
            site.setLat(latlon.X());
            site.setLon(latlon.Y());
            site.setCoordsMoved(true);
        }

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
     * Call the SiteCenters module to calculate new centers for the sites.
     */
    private static void loadSiteCenters() {
        SiteCenters.load(sites.values());
    }

    /**
     *
     * @return True if work was done
     */
    private static void loadLegendsXML() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try (Reader reader = new InputStreamReader(new FileInputStream(ExportFiles.getLegendsXML()), "UTF-8")){
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

    /**
     * Load population info for each site from the pops txt export.
     */
    private static void loadPopulationInfo() {
        // The last read site
        Site lastSite = null;

        // The matcher object for the regex
        Matcher match;

        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(ExportFiles.getPopulationFile()))) {

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
     * DOCME
     * @return
     */
    public static TreeMap<String, List<Site>> getPopulationDistribution() {
        if (popDistribution == null) loadPopulationDistribution();
        return popDistribution;
    }

    /**
     * Get the total count for all races that can be found in the world.
     * @return
     */
    public static TreeMap<String, Integer> getTotalPopulation() {
        if (totalPop == null) loadPopulationDistribution();
        return totalPop;
    }

    /**
     * DOCME
     * @return
     */
    public static TreeMap<String, Integer> getPopulationCounts() {
        if (maxPop == null) loadPopulationDistribution();
        return maxPop;
    }

    /**
     * DOCME
     */
    private static void loadPopulationDistribution() {
        // Create a map, mapping races to a list of sites where they can be found
        popDistribution = new TreeMap<>();

        // Map the race name to the single biggest population count in a single site
        maxPop = new TreeMap<>();

        // Map to count the total population counts
        totalPop = new TreeMap<>();

        for (Site site : sites.values()) {

            String raceName;
            for (Map.Entry<String, Integer> entry : site.getPopulations().entrySet()) {
                if (nameTransform.containsKey(entry.getKey())) {
                    raceName = nameTransform.get(entry.getKey());
                } else {
                    raceName = entry.getKey();
                }

                // Update maximum population for that race
                if (!maxPop.containsKey(raceName)) {
                    maxPop.put(raceName, entry.getValue());
                } else if (maxPop.get(raceName) < entry.getValue()) {
                    maxPop.put(raceName, entry.getValue());
                }

                if (!popDistribution.containsKey(raceName)) {
                    popDistribution.put(raceName, new LinkedList<>());
                }
                popDistribution.get(raceName).add(site);

                if (!totalPop.containsKey(raceName)) {
                    totalPop.put(raceName, 0);
                }
                totalPop.put(raceName, totalPop.get(raceName) + entry.getValue());
            }
        }
    }

    /**
     * Write the sitesgeo.json for the output. Also contains the translated coordinates for all sites.
     */
    public static void geoJson() {
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
            props.put("img", String.format("/icons/%s.png", site.getType().replace(" ", "_").toLowerCase()));

            // Configure detailed map (if available)
            if (sitemaps.containsKey(site.getId())) {
                SitemapInfo sitemap = sitemaps.get(site.getId());
                // Detailed maps use blocks of 48px size
                int east  = site.getCoords().X() + sitemap.getWidth()  / 2 / 48;
                int west  = site.getCoords().X() - sitemap.getWidth()  / 2 / 48;
                int north = site.getCoords().Y() - sitemap.getHeight() / 2 / 48;
                int south = site.getCoords().Y() + sitemap.getHeight() / 2 / 48;
                Coord2d southWest = xy2LonLat(west, south);
                Coord2d northEast = xy2LonLat(east, north);
                props.put("map_bounds", new double[][] {{southWest.Y(), southWest.X()},
                                                        {northEast.Y(), northEast.X()}});

            }
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
        File sitemapsFile = BuildFiles.getSitemapsIndex();
        try (Input input = new Input(new FileInputStream(sitemapsFile))) {
            return Uristmaps.kryo.readObject(input, HashMap.class);
        } catch (Exception e) {
            Log.warn("WorldSites", "Error when reading sitemaps index file: " + sitemapsFile);
            if (sitemapsFile.exists()) {
                // This might have happened because an update changed the class and it can no longer be read
                // remove the file and re-generate it in the next run.
                sitemapsFile.delete();
                Log.info("WorldSites", "The file has been removed. Please try again.");
            }
            System.exit(1);
        }
        return null;
    }

    /**
     * Return a map, listing all sites by their id.
     * @return
     */
    public static Map<Integer, Site> getSites() {
        if (sites == null) initSites();
        return sites;
    }

    /**
     * Load the sites data from the prepared kryo file.
     */
    private static void initSites() {
        File sitesFile = BuildFiles.getSitesFile();
        try (Input input = new Input(new FileInputStream(sitesFile))) {
            sites = Uristmaps.kryo.readObject(input, HashMap.class);
            return;
        } catch (Exception e) {
            Log.warn("Sites", "Error when reading state file: " + sitesFile);
            if (sitesFile.exists()) {
                // This might have happened because an update changed the class and it can no longer be read
                // remove the file and re-generate it in the next run.
                sitesFile.delete();
                Log.info("Sites", "The file has been removed. Please try again.");
            }
            if (Log.DEBUG) Log.debug("Sites", "Exception", e);
            System.exit(1);
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
