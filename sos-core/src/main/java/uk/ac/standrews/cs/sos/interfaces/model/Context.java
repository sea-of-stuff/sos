package uk.ac.standrews.cs.sos.interfaces.model;

import uk.ac.standrews.cs.sos.interfaces.node.Node;

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
 *     "Policies" : CODE
 * }
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Context extends Manifest {

    /**
     * Return a human-readable name for the context
     * @return
     */
    String getName();


    /**
     * Where to get the data from.
     *
     * @param nodes
     */
    void setSources(Node[] nodes);

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
    Policy[] getPolicies();

    /**
     * Nodes where to run this context
     *
     * @return
     */
    Node[] whereToRun();

    /**
     * Combine this context with another one under the AND logical operator
     * @param context to AND
     * @return a new context
     */
    Context AND(Context context);

    /**
     * * Combine this context with another one under the OR logical operator
     * @param context to OR
     * @return a new context
     */
    Context OR(Context context);

}
