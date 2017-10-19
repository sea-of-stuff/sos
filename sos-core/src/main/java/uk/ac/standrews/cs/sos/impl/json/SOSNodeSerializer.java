package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSNodeSerializer extends JsonSerializer<SOSNode> {

    @Override
    public void serialize(SOSNode node, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        try {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_NODE_GUID, node.guid().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_NODE_SIGNATURE_CERTIFICATE, DigitalSignature.getCertificateString(node.getSignatureCertificate()));
        jsonGenerator.writeStringField(JSONConstants.KEY_NODE_HOSTNAME, node.getHostname());
        jsonGenerator.writeNumberField(JSONConstants.KEY_NODE_PORT, node.getHostAddress().getPort());

        jsonGenerator.writeFieldName(JSONConstants.KEY_NODE_SERVICES);
        jsonGenerator.writeStartObject();
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_STORAGE, node.isStorage());
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_CMS, node.isCMS());
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_DDS, node.isDDS());
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_NDS, node.isNDS());
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_RMS, node.isRMS());
        serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_MMS, node.isMMS());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndObject();

        } catch (CryptoException e) {
            throw new IOException("Unable to Serialise node");
        }
    }

    private void serializeService(JsonGenerator jsonGenerator, String service, boolean isExposed) throws IOException {

        jsonGenerator.writeFieldName(service);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeBooleanField(JSONConstants.KEY_NODE_SERVICE_IS_EXPOSED, isExposed);
        jsonGenerator.writeEndObject();

    }
}
