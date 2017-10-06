package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNodeDeserializer extends JsonDeserializer<SOSNode> {

    @Override
    public SOSNode deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID guid = CommonJson.GetGUID(node, JSONConstants.KEY_NODE_GUID);
            PublicKey signatureCertificate = DigitalSignature.getCertificate(node.get(JSONConstants.KEY_NODE_SIGNATURE_CERTIFICATE).asText());
            String hostname = node.get(JSONConstants.KEY_NODE_HOSTNAME).asText();
            int port = node.get(JSONConstants.KEY_NODE_PORT).asInt();

            boolean isStorage = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_STORAGE);
            boolean isDDS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_DDS);
            boolean isNDS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_NDS);
            boolean isMMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_MMS);
            boolean isCMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_CMS);
            boolean isRMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_RMS);

            return new SOSNode(guid, signatureCertificate, hostname, port, false, isStorage, isDDS, isNDS, isMMS, isCMS, isRMS);

        } catch (GUIDGenerationException | CryptoException e) {
            throw new IOException("Unable to recreate SOSNode");
        }

    }

    private boolean isServiceExposed(JsonNode node, String service) {

        return node.get(JSONConstants.KEY_NODE_SERVICES).get(service).get(JSONConstants.KEY_NODE_SERVICE_IS_EXPOSED).asBoolean();
    }
}
