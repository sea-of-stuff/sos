package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.model.Predicate;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateSerializer extends JsonSerializer<Predicate> {

    @Override
    public void serialize(Predicate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

    }
}
