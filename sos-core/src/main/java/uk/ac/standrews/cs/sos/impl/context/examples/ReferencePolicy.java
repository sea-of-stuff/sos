package uk.ac.standrews.cs.sos.impl.context.examples;

import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BasePolicy;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferencePolicy extends BasePolicy {

    public ReferencePolicy(String policyManifest) {
        super(policyManifest);
    }

    @Override
    public void apply(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {

    }

    @Override
    public boolean satisfied(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {
        return false;
    }
}
