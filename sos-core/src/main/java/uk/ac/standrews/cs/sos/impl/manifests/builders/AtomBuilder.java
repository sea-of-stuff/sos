package uk.ac.standrews.cs.sos.impl.manifests.builders;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.InputStream;

/**
 * TODO - rename to AtomSourceBuilder
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomBuilder {

    private Location location;
    private InputStream inputStream; // FIXME - replace with DATA
    private Data data;

    public boolean isBuildIsSet() {
        return buildIsSet;
    }

    private boolean buildIsSet = false;
    private boolean isLocation = false;
    private boolean isInputStream = false;
    private boolean isData = false;

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

    public AtomBuilder setData(Data data) {
        if (!buildIsSet) {
            this.data = data;
            isData = true;
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

    public Data getData() {
        return data;
    };

    public boolean isData() {
        return isData;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public boolean isInputStream() {
        return isInputStream;
    }

}
