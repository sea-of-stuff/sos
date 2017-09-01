package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;

import java.io.IOException;

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
     * Replicate data at least n-times
     *
     * TODO - have param for canPersist (e.g. replicate only to nodes that can persist data)
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

            try {

                Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
                if (contentManifest.getType().equals(ManifestType.ATOM)) {
                    Data data = ((Atom) contentManifest).getData();

                    NodesCollection nodes = policyActions.getNodes(codomain, NodeType.DDS);
                    policyActions.replicateData(data, nodes, factor);
                }
            } catch (IOException | ManifestNotFoundException e) {
                throw new PolicyException("Policy was unable to replicate data referenced by manifest with guid " + manifest.guid());
            }
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            try {
                Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
                if (contentManifest.getType().equals(ManifestType.ATOM)) {

                    int numberReplicas = policyActions.numberOfReplicas(codomain, contentManifest.guid());
                    return numberReplicas >= factor;
                }

                return true; // policy is always satisfied if not atom
            } catch (ManifestNotFoundException e) {
                throw new PolicyException("Unable to check data replication policy for data referenced by manifest with guid: " + manifest.guid());
            }
        }
    }

    // TODO
    public static class MetadataReplicationPolicy implements Policy {

        @Override
        public void apply(Manifest manifest) throws PolicyException {

        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {
            return false;
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

    public static class GrantAccessPolicy implements Policy {

        private PolicyActions policyActions;
        private IGUID granter;
        private IGUID grantee;

        public GrantAccessPolicy(PolicyActions policyActions, IGUID granter, IGUID grantee) {
            this.policyActions = policyActions;
            this.granter = granter;
            this.grantee = grantee;
        }

        @Override
        public void apply(Manifest manifest) throws PolicyException {

            try {
                Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
                if (contentManifest.getType().equals(ManifestType.ATOM_PROTECTED)) {

                    policyActions.grantAccess((SecureAtom) contentManifest, granter, grantee);
                }

            } catch (RoleNotFoundException | ProtectionException | ManifestNotFoundException e) {
                throw new PolicyException("Policy. Granter " + granter.toMultiHash() + " was unable to grant access to grantee " + grantee.toMultiHash() + " for atom " + manifest.guid());
            }
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            // TODO Get secure manifest and check list of roles/keys
            return false;
        }
    }

    // TODO
    public static class ReplicateAllVersionsPolicy implements Policy {

        @Override
        public void apply(Manifest manifest) throws PolicyException {

        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {
            return false;
        }
    }

    // TODO
    public static class NotifyNodesPolicy implements Policy {

        public NotifyNodesPolicy(PolicyActions policyActions, NodesCollection codomain) {

        }

        @Override
        public void apply(Manifest manifest) throws PolicyException {

        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {
            return false;
        }
    }
}
