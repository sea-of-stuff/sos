package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Predicate;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateSerializer extends JsonSerializer<Predicate> {

    @Override
    public void serialize(Predicate predicate, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {

        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField(JSONConstants.KEY_TYPE, ManifestType.PREDICATE.toString());
        jsonGenerator.writeStringField(JSONConstants.KEY_GUID, predicate.guid().toMultiHash());

        jsonGenerator.writeFieldName(JSONConstants.KEY_COMPUTATIONAL_DEPENDENCIES);
        jsonGenerator.writeTree(predicate.dependencies());

        jsonGenerator.writeStringField(JSONConstants.KEY_PREDICATE, predicate.predicate().asText());

        jsonGenerator.writeEndObject();
    }
}
