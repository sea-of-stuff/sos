package uk.ac.standrews.cs.sos.impl.context.directory;

import java.time.Instant;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextVersionInfo {

    public boolean predicateResult = false; // The last result of the predicate
    public Instant timestamp = Instant.MIN; // Time when the predicate was last apply for this content
    public boolean policySatisfied = false; // Whether the policies of the context has been satisfied or not.
    public boolean evicted = false;

}
