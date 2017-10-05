package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilder {

    private JsonNode fatContextDefinition;

    public ContextBuilder(JsonNode fatContextDefinition) {
        this.fatContextDefinition = fatContextDefinition;
    }

    public JsonNode context(IGUID predicate, Set<IGUID> policies) {
        return fatContextDefinition.get("context");
    }

    public JsonNode predicate() {
        return fatContextDefinition.get("predicate");
    }

    public JsonNode policies() {
        return fatContextDefinition.get("policies");
    }
}
