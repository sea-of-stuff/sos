package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RoleDeserializer extends JsonDeserializer<Role> {

    @Override
    public Role deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            IGUID roleGUID = CommonJson.GetGUID(node, JSONConstants.KEY_GUID);
            IGUID userGUID = CommonJson.GetGUID(node, JSONConstants.KEY_USER);

            String name = node.get(JSONConstants.KEY_NAME).asText();
            String signature = node.get(JSONConstants.KEY_SIGNATURE).textValue();

            return new RoleImpl(userGUID, roleGUID, name, signature);
        } catch (GUIDGenerationException e) {
            throw new IOException(e);
        }

    }
}
