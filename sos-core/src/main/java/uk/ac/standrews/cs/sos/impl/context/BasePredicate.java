package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.manifests.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * This class acts mainly as a wrapper for the Java Predicate object.
 * The wrapper allows us to cleanly handle the predicateManifest under the test function and to apply it for the and/or operators of the context.
 *
 * TODO - and/or operations are not fully implemented yet
 * TODO - how is the predicateManifest converted to a first class entity?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasePredicate extends BasicManifest implements Predicate {

    private JsonNode predicateManifest;
    private long maxAge;

    public BasePredicate(JsonNode predicateManifest, long maxAge) {
        super(ManifestType.PREDICATE);

        this.predicateManifest = predicateManifest;
        this.maxAge = maxAge;

        this.guid = makeGUID();
    }

    @Override
    public long maxAge() {
        return maxAge;
    }

    @Override
    public boolean test(IGUID guid) {
        return false;
    }

    @Override
    public Predicate and(Predicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicateManifest.and(other.predicateManifest()), newMaxAge);
    }

    @Override
    public Predicate or(Predicate other) {
        Objects.requireNonNull(other);

        long newMaxAge = maxAge < other.maxAge() ? maxAge : other.maxAge();

        return null; // new SOSPredicateImpl(predicateManifest.or(other.predicateManifest()), newMaxAge);
    }

    @Override
    public boolean verifySignature(Role role) throws SignatureException {
        return false;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() throws IOException {
        return IO.StringToInputStream(predicateManifest.toString());
    }

    @Override
    public JsonNode dependencies() {
        return predicateManifest.get(JSONConstants.KEY_COMPUTATIONAL_DEPENDENCIES);
    }

    @Override
    public JsonNode predicate() {
        return predicateManifest.get(JSONConstants.KEY_PREDICATE);
    }

}
