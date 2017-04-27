package uk.ac.standrews.cs.sos.experiments;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextStats {

    private int size; // in bytes
    private int sizeDataStructures; // in bytes

    private int timeToRunPredicates;
    private int timeToRunPolicies;
    private int timeToRunOverall;

    private int numberOfSources;
    private int numberOfPolicies;
    private int numberOfSpawningNodes;

    // TODO - have this field at this level?
    private int numberOfContexts;
}
