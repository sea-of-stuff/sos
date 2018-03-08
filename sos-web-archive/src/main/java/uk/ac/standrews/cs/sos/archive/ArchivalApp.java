package uk.ac.standrews.cs.sos.archive;

import org.apache.cxf.endpoint.Server;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * TODO - re-crawl pages after some time and update assets
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ArchivalApp {

    private static final int TIME_FOR_NEW_VERSION = 120; // in seconds
    private static final String TEXT_TYPE = "text/";
    private static final String APP_XML_TYPE = "application/xml";
    private static final String APP_XHTML_TYPE = "application/xhtml+xml";

    private static IGUID webInvariant;
    private static HashMap<String, Long> visitedSites;

    public static void main(String[] args) throws ConfigurationException, ServiceException {

        File configFile = new File("example_config.json");
        SettingsConfiguration configuration = new SettingsConfiguration(configFile);

        SOSLocalNode sos = ServerState.init(configuration.getSettingsObj());
        assert(sos != null);

        webInvariant = webAsset(sos);
        visitedSites = new LinkedHashMap<>();

        Queue<String> endPoints = new LinkedList<>();
        endPoints.add("https://sic2.me");
        // endPoints.add("https://en.wikipedia.org/wiki/Main_Page");
        // endPoints.add("http://dmoztools.net/");

        while(!endPoints.isEmpty()) {
            try {
                crawl(sos, endPoints);
            } catch (IOException | ServiceException | URISyntaxException | CrawlerException e) {
                System.err.println("Crawling exception: " + e.getMessage());
            }
        }

        ServerState.kill();
    }

    private static void crawl(SOSLocalNode sos, Queue<String> endPoints) throws IOException, URISyntaxException, CrawlerException, ServiceException {

        String uriToCrawl = endPoints.poll();
        System.out.println("Crawling " + uriToCrawl);
        visitedSites.put(uriToCrawl, System.nanoTime());

        boolean isHTTPFamily = uriToCrawl.startsWith("http://") || uriToCrawl.startsWith("https://");
        if (isHTTPFamily) {
            addData(sos, uriToCrawl);
        } else {
            System.err.println("Not HTTP Resource " + uriToCrawl);
            throw new CrawlerException();
        }

        URLConnection connection = new URL(uriToCrawl).openConnection();
        String contentType = connection.getHeaderField("Content-Type");
        boolean isHTML = contentType.startsWith(TEXT_TYPE) || contentType.equals(APP_XML_TYPE) || contentType.equals(APP_XHTML_TYPE);
        if (!isHTML) {
            System.err.println("Resource is not an HTML page " + uriToCrawl);
            throw new CrawlerException();
        }

        Document doc = Jsoup.connect(uriToCrawl).get();

        Elements links = doc.select("a[href]");
        Elements srcs = doc.select("[src]");

        for (Element src : srcs) {
            String srcURI = src.attr("abs:src");
            addData(sos, srcURI);
        }

        for(Element link:links) {
            String linkURI = link.attr("abs:href");

            // TODO - expire key after X time - re-add key to endPoints queue
            if (!visitedSites.containsKey(linkURI)) {
                endPoints.add(linkURI);
            }
        }
    }

    private static IGUID webAsset(SOSLocalNode sos) throws ServiceException {

        CompoundBuilder compoundBuilder = new CompoundBuilder();

        VersionBuilder versionBuilder = new VersionBuilder()
                .setCompoundBuilder(compoundBuilder);

        // TODO - should satisfied if there is already a version for this URI and append version to already existing asset

        Version version = sos.getAgent().addCollection(versionBuilder);
        return version.invariant();
    }

    private static void addData(SOSLocalNode sos, String uri) throws URISyntaxException, ServiceException {
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation(uri));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        // TODO - should satisfied if there is already a version for this URI and append version to already existing asset

        Version version = sos.getAgent().addData(versionBuilder);

        System.out.println("Added version-atom: " + version);
    }

    private static void updateCompound(SOSLocalNode sos, String parent, Content content) {

        System.out.println("Added version-compound: ");
    }
}
