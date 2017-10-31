package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Statistics {

    private boolean experiment;
    private boolean predicate;
    private boolean predicate_dataset;
    private boolean policies;
    private boolean checkPolicies;
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

    public boolean isCheckPolicies() {
        return checkPolicies;
    }

    public void setCheckPolicies(boolean checkPolicies) {
        this.checkPolicies = checkPolicies;
    }

    public boolean isExperiment() {
        return experiment;
    }

    public void setExperiment(boolean experiment) {
        this.experiment = experiment;
    }

    public boolean isPredicate_dataset() {
        return predicate_dataset;
    }

    public void setPredicate_dataset(boolean predicate_dataset) {
        this.predicate_dataset = predicate_dataset;
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
            case checkPolicies: return isCheckPolicies();
            case predicate_dataset: return isPredicate_dataset();
        }

        return false;
    }
}
