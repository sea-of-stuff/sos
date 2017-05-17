package uk.ac.standrews.cs.sos.impl.context.directory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextContent {

    public boolean predicateResult; // The last result of the predicate
    public long timestamp; // Time when the predicate was last run for this content
    public boolean policySatisfied; // Whether the policy of the context has been satisfied or not -- NOTE: which policy?

}