package uk.ac.standrews.cs.sos.interfaces.model;

/**
 * A rule is a task that must be run by a context.
 *
 * Rules are used to enforce control over data within a context
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 * ////////// - send notifications to nodes/users
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Policy {

    /**
     * Run this rule over this asset
     *
     * @param asset
     */
    void run(Asset asset);

    /**
     * If true, the policy will run as soon as there is an update in the context
     * @return
     */
    default boolean runOnUpdate(){ return true; }

    /**
     * Interval time, in seconds, between background runs of the policy?
     * @return
     */
    default int intervalTime() { return 60; }

}
