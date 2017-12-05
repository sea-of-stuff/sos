package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.services.SOSAgent;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureManifest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.IOException;
import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonJson {

    static IGUID GetGUID(JsonNode node, String key) throws GUIDGenerationException {

        String guid = node.get(key).textValue();
        return GUIDFactory.recreateGUID(guid);
    }

    static Set<IGUID> GetGUIDCollection(JsonNode node, String key) throws GUIDGenerationException {

        JsonNode nodes = node.get(key);
        Set<IGUID> retval = null;
        if (nodes != null && nodes.isArray()) {
            retval = new LinkedHashSet<>();
            for(final JsonNode aNode:nodes) {
                IGUID guid = GUIDFactory.recreateGUID(aNode.textValue());
                retval.add(guid);
            }
        }

        return retval;
    }

    public static ArrayNode GUIDSetToJsonArray(Set<IGUID> guids) {

        ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();
        for(IGUID guid:guids) {
            arrayNode.add(guid.toMultiHash());
        }

        return arrayNode;
    }

    static HashMap<IGUID, String> getRolesToKeys(JsonNode node) throws GUIDGenerationException {

        HashMap<IGUID, String> rolesToKeys = new LinkedHashMap<>();
        JsonNode keysNode = node.get(JSONConstants.KEYS_PROTECTION);
        if (keysNode.isArray()) {
            for(final JsonNode keyNode:keysNode) {
                IGUID role = GUIDFactory.recreateGUID(keyNode.get(JSONConstants.KEYS_PROTECTION_ROLE).asText());
                String encryptedKey = keyNode.get(JSONConstants.KEYS_PROTECTION_KEY).asText();

                rolesToKeys.put(role, encryptedKey);
            }
        }

        return rolesToKeys;
    }

    static void serializeKeys(SecureManifest secureManifest, JsonGenerator jsonGenerator) throws IOException {

        jsonGenerator.writeFieldName(JSONConstants.KEYS_PROTECTION);
        jsonGenerator.writeStartArray();

        HashMap<IGUID, String> keysRolesMap = secureManifest.keysRoles();
        for(Map.Entry<IGUID, String> e:keysRolesMap.entrySet()) {

            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField(JSONConstants.KEYS_PROTECTION_ROLE, e.getKey().toMultiHash());
            jsonGenerator.writeStringField(JSONConstants.KEYS_PROTECTION_KEY, e.getValue());

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();

    }

    static NodesCollection getNodesCollection(JsonNode node, String field) {
        JsonNode nodesCollection_n = node.get(field);
        return JSONHelper.JsonObjMapper().convertValue(nodesCollection_n, NodesCollection.class);
    }

    static Pair<String, Role> deserializeSignatureAndRole(JsonNode node) throws GUIDGenerationException, RoleNotFoundException {

        String signature = null;
        Role signer = null;
        if (node.has(JSONConstants.KEY_SIGNATURE) && node.has(JSONConstants.KEY_SIGNER)) {
            signature = node.get(JSONConstants.KEY_SIGNATURE).textValue();
            IGUID signerGUID = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_SIGNER).textValue());
            signer = SOSAgent.instance().getRole(signerGUID);
        }

        return new Pair<>(signature, signer);
    }

}
