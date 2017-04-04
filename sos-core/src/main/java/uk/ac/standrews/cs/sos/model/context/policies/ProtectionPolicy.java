package uk.ac.standrews.cs.sos.model.context.policies;

import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.PolicyComputationType;

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
