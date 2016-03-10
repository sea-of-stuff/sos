package uk.ac.standrews.cs.sos.model.implementations.locations;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

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

        String[] segments = url.getPath().split("/");
        GUID urlMachineId = new GUIDsha1(url.getHost());
        GUID entityId = new GUIDsha1(segments[segments.length - 1]);
        GUID thisMachineId = SeaConfiguration.getInstance().getNodeId();

        if (urlMachineId.equals(thisMachineId)) {
            String path = SeaConfiguration.getInstance().getCacheDataPath();
            FileInputStream fileStream = new FileInputStream(path + entityId);
            return new BufferedInputStream(fileStream);
        }

        // TODO - deal with remote machine-ids!
        return null;
    }

}
