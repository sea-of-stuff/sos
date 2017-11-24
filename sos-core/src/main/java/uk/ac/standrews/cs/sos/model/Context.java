package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.json.ContextDeserializer;
import uk.ac.standrews.cs.sos.impl.json.ContextSerializer;

import java.time.Instant;
import java.util.Set;

/**
 * A context in the SOS is defined as a set of information used to characterise a collection of related entities.
 * Entities are assets belonging to the SOS.
 *
 * A context is unique and is defined by a predicate, which defines what assets belong to the context or not.
 *
 * Immutable components of the context:
 * - predicate
 * - policies
 *
 * Mutable components of the context:
 * - name
 * - content
 * - domain and codomain
 *
 *
 * GUID = hash(name + invariant + previous + content + domain + codomain)
 *
 * NOTE 1: must have only one version
 * NOTE 2: invariant = hash(predicate + policies)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = ContextSerializer.class)
@JsonDeserialize(using = ContextDeserializer.class)
public interface Context extends Versionable, SignedManifest {

    /**
     * GUID of compound to contents
     *
     * @return the reference to the content of this context's version
     */
    IGUID content();

    /**
     * Return a human-readable name for the context
     *
     * Should not have special characters, as it will mess things up...
     *
     * @return name of the context
     */
    String getName();

    /**
     * name-GUID
     *
     * @return the unique name
     */
    String getUniqueName();

    /**
     * The domain of this context
     *
     * @return domain of the context
     */
    NodesCollection domain();

    /**
     * The codomain of this context
     *
     * @return the codomain
     */
    NodesCollection codomain();

    /**
     * Predicate to apply against data.
     * This will define whether data belongs to this context or not
     *
     * @return a ref to the predicate of the context
     */
    IGUID predicate();

    /**
     * Get the max age for the validity for the predicate of this context.
     * The max age is expressed in seconds.
     * The max age is compared against the UTC time.
     *
     * @return the max age
     */
    long maxAge();

    /**
     * Timestamp marking when the context was created
     *
     * @return timestamp
     */
    Instant timestamp();

    /**
     * Return the policies of this context
     *
     * The predicates must be executed in order.
     *
     * @return a set of policy refs
     */
    Set<IGUID> policies();

    String toFATString(Predicate predicate, Set<Policy> policies) throws JsonProcessingException;

}
