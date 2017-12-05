package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.SecureAtomManifest;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureAtomManifestDeserializer extends AtomManifestDeserializer {

    @Override
    public SecureAtom deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            Set<LocationBundle> bundles = getLocations(node);
            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            return new SecureAtomManifest(contentGUID, bundles, rolesToKeys);

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        }
    }
}
