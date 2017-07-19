package uk.ac.standrews.cs.sos.actors.experiments;

import uk.ac.standrews.cs.sos.actors.ContextService;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextServiceExperiment extends ContextService {

    /**
     * Run the predicates only against all the versions marked as HEADs in the node
     * @return the total number of times that any predicate has been run
     */
    int runPredicates();
}
