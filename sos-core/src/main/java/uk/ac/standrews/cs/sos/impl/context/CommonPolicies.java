package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Policy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonPolicies {

    /**
     * Replicate manifests at least n-times
     */
    public static class ManifestReplicationPolicy implements Policy {

        private PolicyActions policyActions;
        private NodesCollection codomain;
        private int factor;

        public ManifestReplicationPolicy(PolicyActions policyActions, NodesCollection codomain, int factor) {
            this.policyActions = policyActions;
            this.codomain = codomain;
            this.factor = factor;
        }

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            NodesCollection nodes = policyActions.getNodes(codomain, NodeType.DDS);
            policyActions.replicateManifest(manifest, nodes, factor);
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            int numberReplicas = policyActions.numberOfReplicas(codomain, manifest.guid());
            return numberReplicas >= factor;
        }
    }

    /**
     * TODO - need access to the actual data
     * Replicate data at least n-times
     */
    public static class DataReplicationPolicy implements Policy {

        private PolicyActions policyActions;
        private NodesCollection codomain;
        private int factor;

        public DataReplicationPolicy(PolicyActions policyActions, NodesCollection codomain, int factor) {
            this.policyActions = policyActions;
            this.codomain = codomain;
            this.factor = factor;
        }

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            NodesCollection nodes = policyActions.getNodes(codomain, NodeType.DDS);
            policyActions.replicateData(null, nodes, factor); // FIXME - should be able to get a handle on the data
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            int numberReplicas = policyActions.numberOfReplicas(codomain, manifest.guid());
            return numberReplicas >= factor;
        }
    }

    /**
     * Delete content (data or manifest) from some nodes
     */
    public static class DeletionPolicy implements Policy {

        private PolicyActions policyActions;
        private NodesCollection codomain;

        public DeletionPolicy(PolicyActions policyActions, NodesCollection codomain) {
            this.policyActions = policyActions;
            this.codomain = codomain;
        }

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            // TODO - change code so that we get all the nodes (within codomain) where data is
            // and then we delete data from there

            IGUID fakeNodeGUID = GUIDFactory.generateRandomGUID(); // FIXME - have a sensible Node GUID

            boolean hasData = policyActions.nodeHasData(fakeNodeGUID, manifest.guid());

            if (hasData) {
                policyActions.deleteData(manifest.guid(), fakeNodeGUID);
            }
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            int numberReplicas = policyActions.numberOfReplicas(codomain, manifest.guid());
            return numberReplicas == 0;
        }
    }
}
