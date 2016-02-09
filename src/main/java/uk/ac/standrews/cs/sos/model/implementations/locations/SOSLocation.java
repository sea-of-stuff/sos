package uk.ac.standrews.cs.sos.model.implementations.locations;

import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Objects;

/**
 * sos://machine-id/object-GUID
 * sos://machine-id/object-GUID/part
 *
 * How to register custom schemes in Java
 * http://stackoverflow.com/questions/26363573/registering-and-using-a-custom-java-net-url-protocol
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocation implements Location {

    private final static String SOS_SCHEME = "sos";
    private final static String SCHEME_DIVIDER = "://";

    private GUID machineID;
    private GUID entity;
    private URL url;

    public SOSLocation(GUID machineID, GUID entity) throws MalformedURLException {
        this.machineID = machineID;
        this.entity = entity;
        url = new URL("sos://" + machineID.toString() + "/" + entity.toString());
    }

    public SOSLocation(String location) throws MalformedURLException {
        String[] segments = location.split(SCHEME_DIVIDER)[1].split("/");
        this.machineID = new GUIDsha1(segments[0]);
        this.entity = new GUIDsha1(segments[1]);
        url = new URL(location);
    }

    @Override
    public URI getURI() throws URISyntaxException {
        return url.toURI();
    }

    @Override
    public InputStream getSource() throws IOException {
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    @Override
    public String toString() {
        return SOS_SCHEME + SCHEME_DIVIDER + machineID.toString() + "/" + entity.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SOSLocation that = (SOSLocation) o;
        return Objects.equals(machineID, that.machineID) &&
                Objects.equals(entity, that.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(machineID, entity);
    }
}
