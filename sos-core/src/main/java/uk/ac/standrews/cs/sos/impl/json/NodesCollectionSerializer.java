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
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_NODES_COLLECTION_REFS;
import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_NODES_COLLECTION_TYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionSerializer extends JsonSerializer<NodesCollection> {

    @Override
    public void serialize(NodesCollection nodesCollection, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(KEY_NODES_COLLECTION_TYPE, nodesCollection.type().toString());

        jsonGenerator.writeFieldName(KEY_NODES_COLLECTION_REFS);
        jsonGenerator.writeStartArray();
        if (nodesCollection.nodesRefs() != null) {
            for (IGUID guid : nodesCollection.nodesRefs()) {
                jsonGenerator.writeString(guid.toMultiHash());
            }
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
