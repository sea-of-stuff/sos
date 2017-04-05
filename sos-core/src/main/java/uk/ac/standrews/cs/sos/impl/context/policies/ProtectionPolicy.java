package uk.ac.standrews.cs.sos.impl.context.policies;

import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.PolicyComputationType;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ProtectionPolicy implements Policy {
    @Override
    public void run(Manifest manifest) {

    }

    @Override
    public boolean check() {
        return false;
    }

    @Override
    public PolicyComputationType computationType() {
        return null;
    }
}
