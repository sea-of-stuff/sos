package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.Version;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReplicationPolicy implements Policy {

    private int factor;

    public ReplicationPolicy(int factor) {
        this.factor = factor;
    }

    @Override
    public void run(Version version) {

        // TODO make sure that data is replicate x times
    }
}
