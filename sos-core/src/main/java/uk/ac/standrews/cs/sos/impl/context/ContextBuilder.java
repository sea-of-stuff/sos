package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.model.CompoundType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilder {

    private JsonNode fatContextDefinition;

    // TODO - have constructor thin context definition too?!

    public ContextBuilder(JsonNode fatContextDefinition) {
        this.fatContextDefinition = fatContextDefinition;
    }

    public JsonNode context(IGUID predicate, Set<IGUID> policies) {

        JsonNode context = fatContextDefinition.get("context");

        ((ObjectNode)context).put(JSONConstants.KEY_CONTEXT_PREDICATE, predicate.toMultiHash());
        ArrayNode arrayNode = ((ObjectNode)context).putArray(JSONConstants.KEY_CONTEXT_POLICIES);
        for(IGUID policy:policies) {
            arrayNode.add(policy.toMultiHash());
        }

        IGUID content;
        try {
            // Reference to empty compound
            content = new CompoundManifest(CompoundType.COLLECTION, new LinkedHashSet<>(), null).guid();
        } catch (ManifestNotMadeException e) {
            content = new InvalidID();
        }

        ((ObjectNode)context).put(JSONConstants.KEY_CONTEXT_CONTENT, content.toMultiHash());

        return context;
    }

    public JsonNode predicate() {
        return fatContextDefinition.get("predicate");
    }

    public JsonNode policies() {
        return fatContextDefinition.get("policies");
    }
}
