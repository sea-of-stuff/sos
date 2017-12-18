package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.Objects;

/**
 * This class acts mainly as a wrapper for the Java Predicate object.
 * The wrapper allows us to cleanly handle the predicateManifest under the test function and to apply it for the and/or operators of the context.
 *
 * TODO - and/or operations are not fully implemented yet
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasePredicate extends BasicManifest implements Predicate {

    private JsonNode predicateManifest;

    public BasePredicate(JsonNode predicateManifest) {
        super(ManifestType.PREDICATE);

        this.predicateManifest = predicateManifest;

        this.guid = makeGUID();
    }

    @Override
    public boolean test(IGUID guid) {
        return false;
    }

    @Override
    public Predicate and(Predicate other) {
        Objects.requireNonNull(other);

        return null; // new SOSPredicateImpl(predicateManifest.and(other.predicateManifest()), newMaxAge);
    }

    @Override
    public Predicate or(Predicate other) {
        Objects.requireNonNull(other);

        return null; // new SOSPredicateImpl(predicateManifest.or(other.predicateManifest()), newMaxAge);
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() {
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
