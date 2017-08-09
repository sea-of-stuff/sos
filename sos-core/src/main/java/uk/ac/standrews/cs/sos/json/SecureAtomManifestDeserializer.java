package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifestDeserializer extends JsonDeserializer<SecureAtomManifest> {

    @Override
    public SecureAtomManifest deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);

            Set<LocationBundle> bundles = new LinkedHashSet<>();
            JsonNode bundlesNode = node.get(JSONConstants.KEY_LOCATIONS);
            if (bundlesNode.isArray()) {
                for(final JsonNode bundleNode:bundlesNode) {
                    LocationBundle bundle = JSONHelper.JsonObjMapper().convertValue(bundleNode, LocationBundle.class);
                    bundles.add(bundle);
                }
            }

            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            return new SecureAtomManifest(contentGUID, bundles, rolesToKeys);

        } catch (GUIDGenerationException | ManifestNotMadeException e) {
            throw new IOException("Unable to recreate GUID");
        }
    }
}
