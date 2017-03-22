package uk.ac.standrews.cs.sos.interfaces.model;

/**
 * A policy is a task that must be run by a context.
 *
 * Policies are used to enforce control over data within a context
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Policy {

    /**
     * Run this policy over a manifest
     *
     * @param manifest
     */
    void run(Manifest manifest);

    /**
     * Check that the policy is satisfied
     *
     * @return true if the policy is satisfied
     */
    boolean check();
}
