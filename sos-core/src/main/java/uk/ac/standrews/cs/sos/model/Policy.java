package uk.ac.standrews.cs.sos.model;

/**
 * A policy is a task run on the content of a given context.
 * Policies are used to enforce control over content of a given context.
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 * - migrate data from S3 to Azure
 *
 * Policies are run within a given scope (see @link Scope)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Policy {

    /**
     * Run this policy over a manifest
     *
     * @param manifest over which this policy runs
     * @return true if the policy was run/queued. The result of this method is not a result of success of the actual policy.
     * @see #check(Manifest) for info about the success of the policy
     */
    boolean run(Manifest manifest);

    /**
     * Check that the policy is satisfied
     *
     * @param manifest over which this policy will check its agreement
     * @return true if the policy is satisfied
     */
    boolean check(Manifest manifest);

}
