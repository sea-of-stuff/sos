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
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.metadata.MetaType;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static uk.ac.standrews.cs.sos.impl.json.CommonJson.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataDeserializer extends JsonDeserializer<Metadata> {

    @Override
    public Metadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID guid = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            HashMap<String, Property> metadata = getMetadata(node, false);

            String signature = getSignature(node);
            IGUID signerRef = getSignerRef(node);
            Role signer = getSigner(signerRef);

            if (signer == null) {
                return new MetadataManifest(guid, metadata, signerRef, signature);
            } else {
                return new MetadataManifest(guid, metadata, signer, signature);
            }

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to recreate Metadata Manifest");
        }
    }

    protected HashMap<String, Property> getMetadata(JsonNode node, boolean encrypted) {
        HashMap<String, Property> metadata = new HashMap<>();

        JsonNode properties = node.get(JSONConstants.KEY_META_PROPERTIES);
        Iterator<JsonNode> it = properties.elements();
        while (it.hasNext()) {
            JsonNode n = it.next();

            String key = n.get(JSONConstants.KEY_META_KEY).asText();
            String type = n.get(JSONConstants.KEY_META_TYPE).asText();

            Property property = getObject(n.get(JSONConstants.KEY_META_VALUE), type, key, encrypted);
            metadata.put(key, property);
        }

        return metadata;
    }

    private Property getObject(JsonNode element, String type, String key, boolean encrypted) {

        if (encrypted) {
            return new Property(MetaType.get(type), key, element.asText());
        }

        MetaType metaType = MetaType.get(type);
        switch(metaType) {
            case LONG:
                return new Property(key, element.asLong());
            case DOUBLE:
                return new Property(key, element.asDouble());
            case BOOLEAN:
                return new Property(key, element.asBoolean());
            case STRING:
                return new Property(key, element.asText());
            case GUID:
                try {
                    return new Property(key, GUIDFactory.recreateGUID(element.asText()));
                } catch (GUIDGenerationException e) {
                    return new Property(key, new InvalidID());
                }
        }

        return null;
    }
}
