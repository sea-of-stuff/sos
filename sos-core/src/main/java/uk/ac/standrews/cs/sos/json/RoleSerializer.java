package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import uk.ac.standrews.cs.sos.interfaces.Role;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleSerializer  extends JsonSerializer<Role> {

    @Override
    public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

    }
}
