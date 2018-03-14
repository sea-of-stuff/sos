/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeSerializer extends JsonSerializer<Node> {

    @Override
    public void serialize(Node node, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        try {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, node.getType().toString());
            jsonGenerator.writeStringField(JSONConstants.KEY_GUID, node.guid().toMultiHash());

            jsonGenerator.writeStringField(JSONConstants.KEY_SIGNATURE_CERTIFICATE, DigitalSignature.getCertificateString(node.getSignatureCertificate()));
            jsonGenerator.writeStringField(JSONConstants.KEY_NODE_HOSTNAME, node.getIP());
            jsonGenerator.writeNumberField(JSONConstants.KEY_NODE_PORT, node.getHostAddress().getPort());

            jsonGenerator.writeFieldName(JSONConstants.KEY_NODE_SERVICES);
            jsonGenerator.writeStartObject();
            serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_STORAGE, node.isStorage());
            serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_CMS, node.isCMS());
            serializeService(jsonGenerator, JSONConstants.KEY_NODE_SERVICES_MDS, node.isMDS());
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
