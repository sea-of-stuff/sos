package uk.ac.standrews.cs.sos.impl.context.examples;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.impl.context.BasePolicy;
import uk.ac.standrews.cs.sos.impl.context.CommonPolicies;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ReferenceGrantPolicy extends BasePolicy {

    private IGUID granter = GUIDFactory.generateRandomGUID();
    private IGUID grantee = GUIDFactory.generateRandomGUID();;

    public ReferenceGrantPolicy(JsonNode policyManifest) {
        super(policyManifest);
    }

    @Override
    public void apply(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {

        CommonPolicies.grantAccessToAtom(codomain, policyActions, manifest, granter, grantee);
    }

    @Override
    public boolean satisfied(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {

        return CommonPolicies.checkAtomManifestIsProtected(codomain, policyActions, manifest, granter, grantee);
    }
}
