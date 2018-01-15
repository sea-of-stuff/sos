package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Statistics {

    private boolean experiment;
    private boolean predicate;
    private boolean predicate_remote;
    private boolean policies;
    private boolean checkPolicies;
    private boolean io;
    private boolean guid_data;
    private boolean guid_manifest;
    private boolean ping;
    private boolean thread;

    // Needed to automatically parse its JSON string into an object
    public Statistics() {}

    /**
     * Returns true if we should collects stats about this statsTYPE
     *
     * @param statsTYPE to check
     * @return true if the stats type is enabled
     */
    public boolean isEnabled(StatsTYPE statsTYPE) {

        switch (statsTYPE) {
            case any:
                return true;
            case experiment:
                return isExperiment();
            case predicate:
                return isPredicate();
            case predicate_remote:
                return isPredicate_remote();
            case policies:
                return isPolicies();
            case checkPolicies:
                return isCheckPolicies();
            case io:
                return isIo();
            case guid_data:
                return isGuid_data();
            case guid_manifest:
                return isGuid_manifest();
            case ping:
                return isPing();
            case thread:
                return isThread();
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

    public boolean isGuid_data() {
        return guid_data;
    }

    public void setGuid_data(boolean guid_data) {
        this.guid_data = guid_data;
    }

    public boolean isGuid_manifest() {
        return guid_manifest;
    }

    public void setGuid_manifest(boolean guid_manifest) {
        this.guid_manifest = guid_manifest;
    }

    public boolean isPredicate_remote() {
        return predicate_remote;
    }

    public void setPredicate_remote(boolean predicate_remote) {
        this.predicate_remote = predicate_remote;
    }

    public boolean isPing() {
        return ping;
    }

    public void setPing(boolean ping) {
        this.ping = ping;
    }

    public boolean isThread() {
        return thread;
    }

    public void setThread(boolean thread) {
        this.thread = thread;
    }
}
