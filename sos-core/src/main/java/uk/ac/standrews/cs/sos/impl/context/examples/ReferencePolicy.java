package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BasePolicy;
import uk.ac.standrews.cs.sos.model.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferencePolicy extends BasePolicy {

    @Override
    public void apply(Manifest manifest) throws PolicyException {

    }

    @Override
    public boolean satisfied(Manifest manifest) throws PolicyException {
        return false;
    }
}
