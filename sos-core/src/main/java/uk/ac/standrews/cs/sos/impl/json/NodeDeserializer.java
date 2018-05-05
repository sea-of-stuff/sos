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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.security.PublicKey;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeDeserializer extends JsonDeserializer<Node> {

    @Override
    public SOSNode deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID guid = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);

            PublicKey d_publicKey = DigitalSignature.getCertificate(node.get(JSONConstants.KEY_SIGNATURE_CERTIFICATE).asText());
            String hostname = node.get(JSONConstants.KEY_NODE_HOSTNAME).asText();
            int port = node.get(JSONConstants.KEY_NODE_PORT).asInt();

            boolean isStorage = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_STORAGE);
            boolean isMDS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_MDS);
            boolean isNDS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_NDS);
            boolean isMMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_MMS);
            boolean isCMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_CMS);
            boolean isRMS = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_RMS);
            boolean isExperiment = isServiceExposed(node, JSONConstants.KEY_NODE_SERVICES_EXPERIMENT);

            return new SOSNode(guid, d_publicKey, hostname, port, false, isStorage, isMDS, isNDS, isMMS, isCMS, isRMS, isExperiment);

        } catch (GUIDGenerationException | CryptoException e) {
            throw new IOException("Unable to recreate SOSNode");
        }

    }

    private boolean isServiceExposed(JsonNode node, String service) {

        if (!node.has(JSONConstants.KEY_NODE_SERVICES)) return false;

        if (!node.get(JSONConstants.KEY_NODE_SERVICES).has(service)) return false;

        return node.get(JSONConstants.KEY_NODE_SERVICES).get(service).get(JSONConstants.KEY_NODE_SERVICE_IS_EXPOSED).asBoolean();
    }
}
