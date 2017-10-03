package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;

/**
 * A policy is a task apply on the content of a given context.
 * Policies are used to enforce control over content of a given context.
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 * - migrate data from S3 to Azure
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Policy extends Manifest {

    /**
     * Run this policy over a manifest
     *
     * @param codomain over which to apply the policy
     * @param policyActions
     * @param manifest over which this policy runs
     * @throws PolicyException if an error occurred while applying the policy
     * @see #satisfied(NodesCollection, PolicyActions, Manifest) for info about the success of the policy
     */
    void apply(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException;

    /**
     * Check that the policy is satisfied
     *
     * @param codomain over which the policy must be satisfied
     * @param policyActions
     * @param manifest over which this policy will satisfied its agreement
     * @return true if the policy is satisfied
     * @throws PolicyException if an error occurred while checking that the policy is satisfied
     */
    boolean satisfied(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException;

}
