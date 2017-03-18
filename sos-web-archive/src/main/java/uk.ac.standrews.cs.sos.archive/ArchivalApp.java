package uk.ac.standrews.cs.sos.archive;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.model.Atom;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ArchivalApp {

    public static void main(String[] args) throws Exception {

        File configFile = new File(args[0]);
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        SOSLocalNode sos = ServerState.init(configuration);

        Queue<String> endPoints = new LinkedList<>();
        String endPoint = "https://sic2.github.io/";

        endPoints.add(endPoint);
        while(!endPoints.isEmpty()) {
            String uriToCrawl = endPoints.poll();
            System.err.println("Crawling " + uriToCrawl);

            boolean isHTTPFamily = uriToCrawl.startsWith("http://") || uriToCrawl.startsWith("https://");
            if (isHTTPFamily) {
                addData(sos, uriToCrawl);
            } else {
                System.err.println("Not HTTP Resource " + uriToCrawl);
                continue;
            }

            URLConnection connection = new URL(uriToCrawl).openConnection();
            String contentType = connection.getHeaderField("Content-Type");
            boolean isHTML = contentType.startsWith("text/") || contentType.equals("application/xml") || contentType.equals("application/xhtml+xml");
            if (!isHTML) {
                System.err.println("Resource is not an HTML page " + uriToCrawl);
                continue;
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
                endPoints.add(linkURI);
            }
        }

    }

    private static void addData(SOSLocalNode sos, String uri) throws URISyntaxException, ManifestPersistException, StorageException {
        AtomBuilder builder = new AtomBuilder().setLocation(new URILocation(uri));
        Atom atom = sos.getAgent().addAtom(builder);

        System.err.println("Added atom " + atom);
    }
}
