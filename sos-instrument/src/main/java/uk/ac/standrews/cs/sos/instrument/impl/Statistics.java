package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Statistics {

    private boolean experiment;
    private boolean predicate;
    private boolean policies;
    private boolean io_store; // any IO operations on the store
    private boolean network; // any network operations

    public Statistics() {}

    public boolean isPredicate() {
        return predicate;
    }

    public void setPredicate(boolean predicate) {
        this.predicate = predicate;
    }

    public boolean isPolicies() {
        return policies;
    }

    public void setPolicies(boolean policies) {
        this.policies = policies;
    }

    public boolean isExperiment() {
        return experiment;
    }

    public void setExperiment(boolean experiment) {
        this.experiment = experiment;
    }

    /**
     * Returns true if we should collects stats about this statsTYPE
     * @param statsTYPE
     * @return
     */
    public boolean isEnabled(StatsTYPE statsTYPE) {

        switch (statsTYPE) {
            case any: return true;
            case experiment: return isExperiment();
            case predicate: return isPredicate();
            case policies: return isPolicies();
        }

        return false;
    }
}
