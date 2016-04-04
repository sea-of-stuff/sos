package uk.ac.standrews.cs.sos.model.locations.sos.url;

import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.utils.GUIDFactory;
import uk.ac.standrews.cs.utils.IGUID;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLConnection extends URLConnection {

    /**
     * Constructs a URL connection to the specified URL. A connection to
     * the object referenced by the URL is not created.
     *
     * @param url the specified URL.
     */
    protected SOSURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        System.out.println("Connected!");
    }

    @Override
    public InputStream getInputStream() throws IOException {

        try {
            String[] segments = url.getPath().split("/");
            IGUID urlMachineId = GUIDFactory.recreateGUID(url.getHost());
            IGUID entityId = GUIDFactory.recreateGUID(segments[segments.length - 1]);
            IGUID thisMachineId = SeaConfiguration.getInstance().getNodeId();

            if (urlMachineId.equals(thisMachineId)) {
                String path = SeaConfiguration.getInstance().getCacheDataPath();
                FileInputStream fileStream = new FileInputStream(path + entityId);
                return new BufferedInputStream(fileStream);
            }
        } catch (SeaConfigurationException | GUIDGenerationException e) {
            throw new IOException(); // FIXME - this try/catch is a bit dirty.
        }

        /*
         * lookup for node id in local map
         * otherwise contact registry/coordinator
         * talk to coordinator via http
         */
        return null;
    }

}
