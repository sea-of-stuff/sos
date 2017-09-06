package uk.ac.standrews.cs.sos.impl.context.directory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextVersionInfo {

    public boolean predicateResult = false; // The last result of the predicate
    public long timestamp = 0; // Time when the predicate was last apply for this content
    public boolean policySatisfied = false; // Whether the policy of the context has been satisfied or not -- NOTE: this assumes that the context has only one policy

}
