package uk.ac.standrews.cs.sos.impl.context.examples;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BasePolicy;
import uk.ac.standrews.cs.sos.impl.context.CommonPolicies;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferenceDataReplicationPolicy extends BasePolicy {

    private int factor = 1;

    public ReferenceDataReplicationPolicy(JsonNode policyManifest) {
        super(policyManifest);
    }

    @Override
    public void apply(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) throws PolicyException {

        CommonPolicies.replicateData(codomain, commonUtilities, manifest, factor);
    }

    @Override
    public boolean satisfied(NodesCollection codomain, CommonUtilities commonUtilities, Manifest manifest) throws PolicyException {

        return CommonPolicies.dataIsReplicated(codomain, commonUtilities, manifest, factor);
    }
}
