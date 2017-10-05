package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.context.ContextManifest;
import uk.ac.standrews.cs.sos.model.Context;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextDeserializer extends JsonDeserializer<Context> {

    @Override
    public Context deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {


        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            String name = node.get(JSONConstants.KEY_CONTEXT_NAME).asText();

            // TODO - deserialize DOMAIN/CODOMAIN

            IGUID predicate = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_CONTEXT_PREDICATE).asText());

            Set<IGUID> policies = new LinkedHashSet<>();
            JsonNode policies_n = node.get(JSONConstants.KEY_CONTEXT_POLICIES);
            for(JsonNode policy_n:policies_n) {

                IGUID policy = GUIDFactory.recreateGUID(policy_n.asText());
                policies.add(policy);
            }


            Context context = new ContextManifest(name, null, null, predicate, policies, null);
            return context;

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to generate GUIDs for context");
        }

    }
}
