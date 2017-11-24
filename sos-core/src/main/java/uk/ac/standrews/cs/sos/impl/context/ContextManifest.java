package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextManifest extends AbstractSignedManifest implements Context {

    protected Instant timestamp;
    protected String name;
    protected IGUID guid;
    protected IGUID invariant;
    protected IGUID previous;
    protected IGUID predicate;
    private long maxAge;
    protected Set<IGUID> policies;
    protected NodesCollection domain;
    protected NodesCollection codomain;
    protected IGUID content;

    /**
     * The predicate is true only at the time when it is computed.
     */
    public static final long PREDICATE_ALWAYS_TO_COMPUTE = 0;

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

        this.timestamp = Instant.now();
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

    public ContextManifest(Instant timestamp, String name, NodesCollection domain, NodesCollection codomain,
                           IGUID predicate, long maxAge, Set<IGUID> policies, Role signer,
                           IGUID content, IGUID invariant, IGUID previous) {
        super(signer, ManifestType.CONTEXT);

        this.timestamp = timestamp;
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

        if (previous != null && !previous.isInvalid()) {
            contentToHash += "P" + previous.toMultiHash();
        }

        contentToHash += "C" + content().toMultiHash();
        contentToHash += "DO" + domain().toUniqueString();
        contentToHash += "CO" + codomain().toUniqueString();

        return IO.StringToInputStream(contentToHash);
    }

    @Override
    public Set<IGUID> previous() {
        Set<IGUID> prev = new LinkedHashSet<>();
        if (!previous.isInvalid()) {
            prev.add(previous);
        }

        return prev;
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
    public Instant timestamp() {
        return timestamp;
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

    @Override
    public String toFATString(Predicate predicate, Set<Policy> policies) throws JsonProcessingException {

        String jsonFATString = ContextBuilder.toFATString(this, predicate, policies);
        return jsonFATString;

    }

    private IGUID makeInvariantGUID() {

        String contentToHash = "PR" + predicate().toMultiHash();

        Set<IGUID> policies = policies();
        if (policies != null && !policies.isEmpty()) {
            contentToHash += "PO" + getCollectionToHashOrSign(policies);
        }

        contentToHash += "MA" + Long.toString(maxAge);

        IGUID guid;
        try {
            guid = GUIDFactory.generateGUID(GUID_ALGORITHM, contentToHash);
        } catch (GUIDGenerationException e) {
            guid = new InvalidID();
        }

        return guid;
    }

}
