package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.impl.context.reflection.SOSReflection;
import uk.ac.standrews.cs.sos.model.Predicate;

import java.io.IOException;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PredicateDeserializer extends JsonDeserializer<Predicate> {

    @Override
    public Predicate deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            SOSReflection.instance().load(node);
            return SOSReflection.instance().predicateInstance(node);

        } catch (ClassLoaderException e) {
            throw new IOException(e);
        }

    }

}
