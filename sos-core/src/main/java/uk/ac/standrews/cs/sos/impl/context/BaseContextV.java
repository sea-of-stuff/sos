package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.manifests.SignedManifest;
import uk.ac.standrews.cs.sos.model.ContextV;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BaseContextV extends SignedManifest implements ContextV {

    private JsonNode jsonNode;

    protected PolicyActions policyActions;

    protected String name;
    protected IGUID guid;
    protected IGUID invariant;
    protected IGUID previous;
    protected IGUID predicate;
    protected Set<IGUID> policies;
    protected NodesCollection domain;
    protected NodesCollection codomain;

    /**
     * The predicate is computed once and its result is true forever.
     */
    protected static final long PREDICATE_ALWAYS_TRUE = Long.MAX_VALUE;
    /**
     * The predicate is true only at the time when it is computed.
     */
    protected static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

    /**
     * Use this constructor when creating a new context object and its GUID is unknown yet
     *
     * @param jsonNode
     * @param policyActions
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContextV(JsonNode jsonNode, PolicyActions policyActions, String name, NodesCollection domain, NodesCollection codomain, Role signer) {
        this(jsonNode, policyActions, GUIDFactory.generateRandomGUID(), name, domain, codomain, signer);
    }

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     * @param jsonNode
     * @param policyActions
     * @param guid
     * @param name
     * @param domain
     * @param codomain
     */
    public BaseContextV(JsonNode jsonNode, PolicyActions policyActions, IGUID guid, String name, NodesCollection domain, NodesCollection codomain, Role signer) {
        super(signer, ManifestType.CONTEXT);

        this.jsonNode = jsonNode;
        this.jsonNode = ((ObjectNode)jsonNode).put("guid", guid.toMultiHash()); // how is the guid known already? guid is not random anymore

        this.policyActions = policyActions;

        this.invariant = makeInvariantGUID();
        this.guid = makeContextGUID();
        // TODO - throw exceptions if invariant or guid are invalid

        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public IGUID invariant() {

        return invariant;
    }

    @Override
    public IGUID content() {

        // TODO - generate this guid earlier
        // GUID to compound of contents
        return new InvalidID();
    }

    @Override
    public InputStream contentToHash() {

        String contentToHash = getType() + getName() + "I" + invariant().toMultiHash();

        if (previous() != null) {
            contentToHash += "P" + previous().toMultiHash();
        }

        contentToHash += "C" + content().toMultiHash();
        contentToHash += "DO" + domain().toUniqueString();
        contentToHash += "CO" + codomain().toUniqueString();

        return IO.StringToInputStream(contentToHash);
    }

    @Override
    public IGUID previous() {
        return previous;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueName() {
        return name + "-" + guid.toMultiHash();
    }

    @Override
    public NodesCollection domain() {
        return domain;
    }

    @Override
    public NodesCollection codomain() {
        return codomain;
    }

    @Override
    public IGUID predicate() {
        return predicate;
    }

    @Override
    public Set<IGUID> policies() {
        return policies;
    }

    @Override
    public boolean verifySignature(Role role) throws SignatureException {
        return false;
    }

    @Override
    protected String generateSignature(String toSign) throws SignatureException {

        if (signer == null) {
            return "";
        } else {
            return signer.sign(toSign);
        }
    }

    // TODO - json representation should be generated using json serialiser
    @Override
    public String toString() {
        return jsonNode.toString();
    }

    private IGUID makeContextGUID() {

        try (InputStream inputStream = contentToHash()) {

            return GUIDFactory.generateGUID(inputStream);

        } catch (GUIDGenerationException | IOException e) {

            return new InvalidID();
        }
    }

    private IGUID makeInvariantGUID() {

        String contentToHash = "PR" + predicate().toMultiHash();

        Set<IGUID> policies = policies();
        if (policies != null && !policies.isEmpty()) {
            contentToHash += "PO" + getCollectionToHashOrSign(policies);
        }

        IGUID guid;
        try {
            guid = GUIDFactory.generateGUID(contentToHash);
        } catch (GUIDGenerationException e) {
            guid = new InvalidID();
        }

        return guid;
    }

}
