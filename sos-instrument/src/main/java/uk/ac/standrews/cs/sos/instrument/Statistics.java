package uk.ac.standrews.cs.sos.instrument;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Statistics {

    private boolean predicate;
    private boolean policies;

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

    public boolean isEnabled(StatsTYPE statsTYPE) {

        switch (statsTYPE) {
            case any: return true;
            case predicate: return isPredicate();
            case policies: return isPolicies();
        }

        return false;
    }
}
