package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.IGUID;

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
 * TODO - rename to Context
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextV extends Manifest {

    /**
     * This is the unique GUID for this context
     *
     * hash(name + invariant + previous + content + domain + codomain)
     *
     * @return GUID of the context
     */
    IGUID guid();

    /**
     * hash(predicate + policies)
     *
     * @return invariant of the context
     */
    IGUID invariant();

    /**
     * GUID of compound to contents
     *
     * @return
     */
    IGUID content();

    /**
     * Optional.
     * Should be GUID of another ContextV
     *
     * @return
     */
    IGUID previous();

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
     * @return
     */
    String getUniqueName();

    /**
     * Return the domain of this context
     *
     * @return domain of the context
     */
    NodesCollection domain();

    NodesCollection codomain();

    /**
     * Predicate to apply against data.
     * This will define whether data belongs to this context or not
     *
     * @return a ref to the predicate of the context
     */
    IGUID predicate();

    /**
     * Return the policies of this context
     *
     * The predicates must be executed in order.
     *
     * @return a set of policy refs
     */
    Set<IGUID> policies();

}
