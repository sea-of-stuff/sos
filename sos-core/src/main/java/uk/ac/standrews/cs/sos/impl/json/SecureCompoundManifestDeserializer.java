package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.SecureCompoundManifest;
import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureCompound;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.deserializeSignatureAndRole;
import static uk.ac.standrews.cs.sos.impl.json.CommonJson.getRolesToKeys;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SecureCompoundManifestDeserializer extends CompoundManifestDeserializer {

    @Override
    public SecureCompound deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID contentGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            Pair<String, Role> signatureRole = deserializeSignatureAndRole(node);
            String compoundTypeString = node.get(JSONConstants.KEY_COMPOUND_TYPE).textValue();
            CompoundType compoundType = CompoundType.valueOf(compoundTypeString);
            Set<Content> contents = getContents(node);
            HashMap<IGUID, String> rolesToKeys = getRolesToKeys(node);

            return new SecureCompoundManifest(compoundType, contentGUID, contents, signatureRole.Y(), signatureRole.X(), rolesToKeys);

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate GUID");
        } catch (RoleNotFoundException e) {
            throw new IOException("Unable to get role signer for secure compound");
        }
    }
}
