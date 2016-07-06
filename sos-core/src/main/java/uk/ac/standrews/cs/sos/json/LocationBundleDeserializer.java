package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.*;
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
        if (type.equals(BundleTypes.CACHE.toString())) {
            ret = new CacheLocationBundle(location);
        } else if (type.equals(BundleTypes.PROVENANCE.toString())) {
            ret = new ProvenanceLocationBundle(location);
        } else if (type.equals(BundleTypes.PERSISTENT.toString())) {
            ret = new PersistLocationBundle(location);
        } else {
            throw new IOException("Unknown location bundle type exception");
        }

        return ret;
    }

}
