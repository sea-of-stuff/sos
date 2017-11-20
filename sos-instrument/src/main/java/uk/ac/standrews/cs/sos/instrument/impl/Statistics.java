package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Statistics {

    private boolean experiment;
    private boolean predicate;
    private boolean policies;
    private boolean checkPolicies;
    private boolean io;

    public Statistics() {
    }

    /**
     * Returns true if we should collects stats about this statsTYPE
     *
     * @param statsTYPE
     * @return
     */
    public boolean isEnabled(StatsTYPE statsTYPE) {

        switch (statsTYPE) {
            case any:
                return true;
            case experiment:
                return isExperiment();
            case predicate:
                return isPredicate();
            case policies:
                return isPolicies();
            case checkPolicies:
                return isCheckPolicies();
            case io:
                return isIo();
        }

        return false;
    }

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

    public boolean isIo() {
        return io;
    }

    public void setIo(boolean io) {
        this.io = io;
    }
}
