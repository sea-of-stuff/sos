package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

/**
 * A context in the SOS is defined as a set of information used to characterise a collection of related entities.
 * Entities are assets belonging to the SOS.
 *
 * A context is unique and is defined by a closure, which defines what assets belong to the context or not.
 * Moreover, contexts can be combines under the boolean operators AND and OR
 *
 * TODO - json serialise/deserialiser
 *
 * Example:
 *
 * {
 *     "Type" : "Context",
 *     "GUID" : "3f845edc76b7e892ddca1f6e290750fe805e7f00",
 *     "Name" : "Simone's replication context",
 *     "Predicate" : CODE,
 *     "Policies" : [ CODE ], // Will be executed in order
 *     "Sources" : [ Where to get the data from ]
 *     "Where" : [ List of nodes where to spawn and apply this context ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Context {

    /**
     * This is the unique GUID for this context
     *
     * @return GUID of the context
     */
    IGUID guid();

    /**
     * Return a human-readable name for the context
     *
     * @return name of the context
     */
    String getName();

    /**
     * Return the domain of this context
     *
     * @return domain of the context
     */
    NodesCollection domain();

    /**
     * Predicate to apply against data.
     * This will define whether data belongs to this context or not
     *
     * @return predicate of the context
     */
    SOSPredicate predicate();

    /**
     * Return the policies of this context
     *
     * The predicates must be executed in order.
     *
     * @return an array of policies
     */
    Policy[] policies();

    /**
     * Nodes where to apply this context
     * This information will be used to spawn the context to such nodes.
     *
     * @return the nodes where to spawn this context
     */
    NodesCollection whereToRun();

}
