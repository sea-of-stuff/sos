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
 *     "Where" : [ List of nodes where to spawn and run this context ]
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Context {

    IGUID guid();

    /**
     * Return a human-readable name for the context
     * @return
     */
    String getName();

    /**
     * Predicate to run against data.
     * This will define whether data belongs to this context or not
     *
     * @return
     */
    SOSPredicate predicate();

    /**
     * Return the policies of this context
     * @return
     */
    Policy[] policies();

    /**
     * Nodes where to run this context
     * This information will be used to spawn the context to such nodes.
     *
     * @return
     */
    NodesCollection whereToRun();

}
