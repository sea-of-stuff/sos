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
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodesCollectionDeserializer extends JsonDeserializer<NodesCollection> {

    @Override
    public NodesCollection deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            NodesCollectionType type = NodesCollectionType.get(node.get(JSONConstants.KEY_NODES_COLLECTION_TYPE).asText());

            Set<IGUID> nodes = new LinkedHashSet<>();
            JsonNode nodes_n = node.get(JSONConstants.KEY_NODES_COLLECTION_REFS);
            for (JsonNode node_n : nodes_n) {
                IGUID ref = GUIDFactory.recreateGUID(node_n.asText());
                nodes.add(ref);
            }

            if (type != NodesCollectionType.SPECIFIED) {
                return new NodesCollectionImpl(type);
            } else {
                return new NodesCollectionImpl(nodes);
            }

        } catch (GUIDGenerationException | NodesCollectionException e) {

            throw new IOException("Unable to deserialize a NodesCollection");
        }

    }

}
