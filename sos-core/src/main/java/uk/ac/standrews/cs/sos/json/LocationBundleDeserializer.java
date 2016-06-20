package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationBundleDeserializer extends JsonDeserializer<LocationBundle> {

    @Override
    public LocationBundle deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext)
            throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String type = node.get(ManifestConstants.BUNDLE_TYPE).textValue();
        String uri = node.get(ManifestConstants.BUNDLE_LOCATION).textValue();

        return makeLocationBundle(type, uri);
    }

    private LocationBundle makeLocationBundle(String type, String uri) throws IOException {
        Location location;
        try {
            if (uri.startsWith("sos")) {
                location = new SOSLocation(uri);
            } else {
                location = new URILocation(uri);
            }
        } catch (URISyntaxException | MalformedURLException e) {
            throw new IOException(e);
        }

        LocationBundle ret;
        switch(type) {
            case BundleTypes.CACHE:
                ret = new CacheLocationBundle(location);
                break;
            case BundleTypes.PROVENANCE:
                ret = new ProvenanceLocationBundle(location);
                break;
            default:
                throw new IOException("Unknown location bundle type exception");
        }

        return ret;
    }

}
