package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.context.ContextBuilderException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.datamodel.CompoundManifest;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.CompoundType;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.context.ContextBuilder.ContextBuilderType.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextBuilder {

    public enum ContextBuilderType {
        FAT, THIN, TEMP
    }

    public ContextBuilderType getContextBuilderType() {
        return contextBuilderType;
    }

    private ContextBuilderType contextBuilderType;

    // FAT && THIN
    private JsonNode contextDefinitions;

    private CompoundManifest compoundManifest;

    // TEMP
    private IGUID previous;
    private Compound contents;
    private NodesCollection domain;
    private NodesCollection codomain;
    private long maxage;


    public ContextBuilder(JsonNode contextDefinition, ContextBuilderType contextBuilderType) {
        this.contextBuilderType = contextBuilderType;
        this.contextDefinitions = contextDefinition;
    }

    public ContextBuilder(IGUID previous, Compound contents, NodesCollection domain, NodesCollection codomain, long maxage) {
        this.contextBuilderType = TEMP;

        this.previous = previous;
        this.contents = contents;
        this.domain = domain;
        this.codomain = codomain;
        this.maxage = maxage;
    }

    public JsonNode context(IGUID predicate, Set<IGUID> policies) throws ContextBuilderException {

        if (contextBuilderType != FAT) {
            throw new ContextBuilderException();
        }

        JsonNode context = contextDefinitions.get("context");

        ((ObjectNode)context).put(JSONConstants.KEY_CONTEXT_PREDICATE, predicate.toMultiHash());
        ArrayNode arrayNode = ((ObjectNode)context).putArray(JSONConstants.KEY_CONTEXT_POLICIES);
        for(IGUID policy:policies) {
            arrayNode.add(policy.toMultiHash());
        }

        IGUID content;
        try {
            // Reference to empty compound
            compoundManifest = new CompoundManifest(CompoundType.COLLECTION, new LinkedHashSet<>(), null);
            content = compoundManifest.guid();
        } catch (ManifestNotMadeException e) {
            content = new InvalidID();
        }

        ((ObjectNode)context).put(JSONConstants.KEY_CONTEXT_CONTENT, content.toMultiHash());

        return context;
    }

    public JsonNode predicate() throws ContextBuilderException {

        if (contextBuilderType == FAT) {
            return contextDefinitions.get("predicate");
        }

        throw new ContextBuilderException();
    }

    public JsonNode policies() throws ContextBuilderException {

        if (contextBuilderType == FAT) {
            return contextDefinitions.get("policies");
        }

        throw new ContextBuilderException();
    }

    public CompoundManifest getCompoundManifest() {
        return compoundManifest;
    }

    public IGUID predicateRef() throws ContextBuilderException {

        if (contextBuilderType == THIN) {
            try {
                return GUIDFactory.recreateGUID(contextDefinitions.get("context").get(JSONConstants.KEY_CONTEXT_PREDICATE).asText());
            } catch (GUIDGenerationException ignored) { }
        }

        throw new ContextBuilderException();
    }

    public Set<IGUID> policyRefs() throws ContextBuilderException {

        if (contextBuilderType == THIN) {
            try {
                Set<IGUID> guids = new LinkedHashSet<>();

                JsonNode context = contextDefinitions.get("context");
                JsonNode policies_n = context.get(JSONConstants.KEY_CONTEXT_POLICIES);
                for(JsonNode policy_n:policies_n) {

                    IGUID guid = GUIDFactory.recreateGUID(policy_n.asText());
                    guids.add(guid);
                }

                return guids;

            } catch (GUIDGenerationException ignored) { }
        }

        throw new ContextBuilderException();
    }

    public IGUID getPrevious() {
        return previous;
    }

    public Compound getContents() {
        return contents;
    }

    public NodesCollection getDomain() {
        return domain;
    }

    public NodesCollection getCodomain() {
        return codomain;
    }

    public long getMaxage() {
        return maxage;
    }
}
