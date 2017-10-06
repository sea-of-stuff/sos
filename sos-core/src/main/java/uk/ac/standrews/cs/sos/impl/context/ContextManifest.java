package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.manifest.SignedManifest;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.Set;

/**
 * TODO - tests
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextManifest extends SignedManifest implements Context {

    protected String name;
    protected IGUID guid;
    protected IGUID invariant;
    protected IGUID previous;
    protected IGUID predicate;
    private long maxAge = 0; // TODO - set from constructor
    protected Set<IGUID> policies;
    protected NodesCollection domain;
    protected NodesCollection codomain;
    protected IGUID content;

    // TODO - have max-age here, not in the predicate
    /**
     * The predicate is computed once and its result is true forever.
     */
    protected static final long PREDICATE_ALWAYS_TRUE = Long.MAX_VALUE;
    /**
     * The predicate is true only at the time when it is computed.
     */
    protected static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

    /**
     * Use this constructor when creating an already existing object with its GUID known already
     *
     * @param name
     * @param domain
     * @param codomain
     */
    public ContextManifest(String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies, Role signer,
                           IGUID content) {
        super(signer, ManifestType.CONTEXT);

        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.previous = new InvalidID();

        this.invariant = makeInvariantGUID();
        this.guid = makeGUID();
        // TODO - throw exceptions if invariant or guid are invalid
    }

    public ContextManifest(String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies, Role signer,
                           IGUID content, IGUID invariant, IGUID previous) {
        super(signer, ManifestType.CONTEXT);

        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.predicate = predicate;
        this.maxAge = maxAge;
        this.policies = policies;
        this.content = content;

        this.previous = previous;

        this.invariant = makeInvariantGUID();
        this.guid = makeGUID();

        if (!this.invariant.equals(invariant)) {
            // TODO - throw exception
        }
        // TODO - throw exceptions if invariant or guid are invalid
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

        // GUID to compound of contents
        return content;
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
    public long maxAge() {
        return maxAge;
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
