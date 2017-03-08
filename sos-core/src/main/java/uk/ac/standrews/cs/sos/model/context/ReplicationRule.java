package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.sos.interfaces.context.Rule;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReplicationRule implements Rule {

    private int factor;

    public ReplicationRule(int factor) {
        this.factor = factor;
    }

    @Override
    public void run(Asset asset) {

        // TODO make sure that data is replicate x times
    }
}
