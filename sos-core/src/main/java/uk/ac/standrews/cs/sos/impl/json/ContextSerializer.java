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

        Set<IGUID> prev = context.previous();
        if (prev != null && !prev.isEmpty()) {
            IGUID previous = prev.iterator().next();
            jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_PREVIOUS, previous.toMultiHash());
        }

        jsonGenerator.writeObjectField(JSONConstants.KEY_CONTEXT_DOMAIN, context.domain());
        jsonGenerator.writeObjectField(JSONConstants.KEY_CONTEXT_CODOMAIN, context.codomain());

        jsonGenerator.writeStringField(JSONConstants.KEY_CONTEXT_PREDICATE, context.predicate().toMultiHash());
        jsonGenerator.writeNumberField(JSONConstants.KEY_CONTEXT_MAX_AGE, context.maxAge());

        jsonGenerator.writeFieldName(JSONConstants.KEY_CONTEXT_POLICIES);
        jsonGenerator.writeStartArray();
        for(IGUID policy:context.policies()) {
            jsonGenerator.writeString(policy.toMultiHash());
        }
        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
