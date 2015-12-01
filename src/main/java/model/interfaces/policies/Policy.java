package model.interfaces.policies;

import model.interfaces.SeaOfStuff;

/**
 * The policy interface describes action and behaviour constraints on entities
 * in the Sea of Stuff.
 *
 * Types of policies:
 * - where data goes
 *
 * - where computation goes
 *
 * - caching
 *
 * - location algorithms
 *
 * - duplication:
 *  stores data on multiple nodes to ensure reliability and fault tolerance
 *  determines the nodes where replicas are placed.
 *  Params: replication factor.
 *
 * - cores
 *
 * - bandwidth
 *
 * - load balancing
 *
 * - failure handling
 *  retrying failed operations.
 *  use alternative solutions on failures.
 *
 * - data recovery
 *
 * @see SeaOfStuff
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Policy {
    // TODO

    /**
     * Adds this policy to a given policy.
     *
     * @param policy to be added to this one.
     * @return new policy.
     */
    Policy add(Policy policy);
}
