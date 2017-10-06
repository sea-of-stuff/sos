package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static uk.ac.standrews.cs.sos.constants.LocationSchemes.SOS_SCHEME;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationFactory {

    public static Location makeLocation(String uri) throws IOException {
        Location location;
        try {
            if (uri.startsWith(SOS_SCHEME)) {
                location = new SOSLocation(uri);
            } else {
                location = new URILocation(uri);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IOException(e);
        }

        return location;
    }
}
