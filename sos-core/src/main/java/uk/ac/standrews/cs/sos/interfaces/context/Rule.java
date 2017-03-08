package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.sos.interfaces.model.Asset;

/**
 * A rule is a task that must be run by a context.
 *
 * Rules are used to enforce control over data within a context
 *
 * Examples:
 * - replicate data to nodes [X]
 * - replicate data at least N times
 * - protect data
 * - send notifications to nodes/users
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Rule {

    /**
     * Run this rule over this asset
     *
     * @param asset
     */
    void run(Asset asset);
}
