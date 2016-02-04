package uk.ac.standrews.cs.sos.model.implementations.locations;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

    public SOSLocation(GUID machineID, GUID entity) {
        this.machineID = machineID;
        this.entity = entity;
    }

    @Override
    public URI getURI() {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getSource() throws IOException {
        throw new NotImplementedException();
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
