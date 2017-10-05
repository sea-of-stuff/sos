package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilder {

    private JsonNode fatContextDefinition;

    // TODO - have thing context definition too!

    public ContextBuilder(JsonNode fatContextDefinition) {
        this.fatContextDefinition = fatContextDefinition;
    }

    public JsonNode context(IGUID predicate, Set<IGUID> policies) {
        // TODO - add refs to context definition
        JsonNode context = fatContextDefinition.get("context");

        // this.jsonNode = ((ObjectNode)jsonNode).put("guid", guid.toMultiHash()); // how is the guid known already? guid is not random anymore

        // TODO - reference to empty compound

        return fatContextDefinition.get("context");
    }

    public JsonNode predicate() {
        return fatContextDefinition.get("predicate");
    }

    public JsonNode policies() {
        return fatContextDefinition.get("policies");
    }
}
