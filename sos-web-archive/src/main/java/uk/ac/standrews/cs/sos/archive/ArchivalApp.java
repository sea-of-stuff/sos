package uk.ac.standrews.cs.sos.archive;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.sos.configuration.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ArchivalApp {

    private static HashMap<String, Long> visitedSites;

    public static void main(String[] args) throws ConfigurationException {

        File configFile = new File(args[0]);
        SettingsConfiguration configuration = new SettingsConfiguration(configFile);

        SOSLocalNode sos = ServerState.init(configuration.getSettingsObj());

        visitedSites = new LinkedHashMap<>();
        Queue<String> endPoints = new LinkedList<>();

        endPoints.add("https://sic2.github.io/");
        endPoints.add("https://en.wikipedia.org/wiki/Main_Page");
        endPoints.add("http://dmoztools.net/");

        while(!endPoints.isEmpty()) {
            try {
                crawl(sos, endPoints);
            } catch (IOException | ManifestPersistException | URISyntaxException | StorageException | CrawlerException e) {
                continue;
            }
        }
    }

    private static void crawl(SOSLocalNode sos, Queue<String> endPoints) throws IOException, ManifestPersistException, StorageException, URISyntaxException, CrawlerException {

        String uriToCrawl = endPoints.poll();
        System.err.println("Crawling " + uriToCrawl);
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
        boolean isHTML = contentType.startsWith("text/") || contentType.equals("application/xml") || contentType.equals("application/xhtml+xml");
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

            if (!visitedSites.containsKey(linkURI)) { // TODO - expire key after X time
                endPoints.add(linkURI);
            }
        }
    }

    private static void addData(SOSLocalNode sos, String uri) throws URISyntaxException, ManifestPersistException, StorageException {
        AtomBuilder atomBuilder = new AtomBuilder().setLocation(new URILocation(uri));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        // TODO - should satisfied if there is already a version for this URI and append version to already existing asset

        Version version = sos.getAgent().addData(versionBuilder);

        System.err.println("Added version-atom " + version);
    }
}
