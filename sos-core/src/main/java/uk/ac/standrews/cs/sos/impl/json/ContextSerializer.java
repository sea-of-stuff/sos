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
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.Context;

import java.io.IOException;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextSerializer extends JsonSerializer<Context> {

    @Override
    public void serialize(Context context, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, context.getType().toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, context.guid().toMultiHash());

        jsonGenerator.writeNumberField(JSONConstants.KEY_CONTEXT_TIMESTAMP, context.timestamp().getEpochSecond());
        jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_NAME, context.getName());
        jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_INVARIANT, context.invariant().toMultiHash());
        jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_CONTENT, context.content().toMultiHash());

        writePrevious(context, jsonGenerator);

        jsonGenerator.writeObjectField(JSONConstants.KEY_CONTEXT_DOMAIN, context.domain(false));
        jsonGenerator.writeObjectField(JSONConstants.KEY_CONTEXT_CODOMAIN, context.codomain());

        jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_PREDICATE, context.predicate().toMultiHash());
        jsonGenerator.writeNumberField(JSONConstants.KEY_CONTEXT_MAX_AGE, context.maxAge());

        writePolicies(context, jsonGenerator);

        jsonGenerator.writeEndObject();
    }

    private void writePrevious(Context context, JsonGenerator jsonGenerator) throws IOException {

        Set<IGUID> prev = context.previous();
        if (prev != null && !prev.isEmpty()) {
            IGUID previous = prev.iterator().next();
            jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_PREVIOUS, previous.toMultiHash());
        }
    }

    private void writePolicies(Context context, JsonGenerator jsonGenerator) throws IOException {

        jsonGenerator.writeFieldName(JSONConstants.KEY_CONTEXT_POLICIES);
        jsonGenerator.writeStartArray();
        for(IGUID policy:context.policies()) {
            jsonGenerator.writeString(policy.toMultiHash());
        }
        jsonGenerator.writeEndArray();
    }
}
