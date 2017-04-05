package uk.ac.standrews.cs.sos.model.context.policies;

import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.PolicyComputationType;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompressPolicy implements Policy {

    @Override
    public void run(Manifest manifest) {
        // COMPRESS THE GIVEN DATA
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    public PolicyComputationType computationType() {
        return null;
    }
}
