package uk.ac.standrews.cs.sos.model.manifests.builders;

import uk.ac.standrews.cs.sos.interfaces.model.Location;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomBuilder {

    private Location location;
    private InputStream inputStream;

    private boolean buildIsSet = false;
    private boolean isLocation = false;
    private boolean isInputStream = false;

    public AtomBuilder setLocation(Location location) {
        if (!buildIsSet) {
            this.location = location;
            isLocation = true;
            buildIsSet = true;
        }

        return this;
    }

    public AtomBuilder setInputStream(InputStream inputStream) {
        if (!buildIsSet) {
            this.inputStream = inputStream;
            isInputStream = true;
            buildIsSet = true;
        }

        return this;
    }

    public Location getLocation() {
        return location;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public boolean isInputStream() {
        return isInputStream;
    }

    @Override
    public String toString() {
        return "{ 'isLocation' : '" + isLocation() + "', " +
                "'isInputStream' : " + isInputStream() + "'}";
    }
}
