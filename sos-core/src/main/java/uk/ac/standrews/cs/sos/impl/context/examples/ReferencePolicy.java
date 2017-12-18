package uk.ac.standrews.cs.sos.impl.context.examples;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.impl.context.BasePolicy;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferencePolicy extends BasePolicy {

    public ReferencePolicy(JsonNode policyManifest) {
        super(policyManifest);
    }

    @Override
    public void apply(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) {
    }

    @Override
    public boolean satisfied(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) {
        return false;
    }
}
