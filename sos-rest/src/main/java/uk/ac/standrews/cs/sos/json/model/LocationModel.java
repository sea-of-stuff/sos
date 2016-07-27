package uk.ac.standrews.cs.sos.json.model;

import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.LocationFactory;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

public class LocationModel {

    private String uri;

    public LocationModel() {}

    public void setUri(String uri) {
        this.uri = uri;

    }

    public String getUri() {
        return uri;
    }

    public Location getLocation() throws IOException {
        return LocationFactory.makeLocation(uri);
    }
}