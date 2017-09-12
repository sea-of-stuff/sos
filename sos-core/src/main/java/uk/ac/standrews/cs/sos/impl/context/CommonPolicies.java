package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;

import java.io.IOException;

/**
 * This class contains some pre-defined policies.
 * Each policy implements the #Policy interface and it has the two methods:
 * - void apply(Manifest manifest)
 * - boolean satisfied(Manifest manifest)
 *
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

            int numberReplicas = policyActions.numberOfManifestReplicas(codomain, manifest.guid());
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

                switch(contentManifest.getType()) {
                    case ATOM:
                        Data data = ((Atom) contentManifest).getData();

                        NodesCollection nodes = policyActions.getNodes(codomain, NodeType.DDS);
                        policyActions.replicateData(data, nodes, factor);

                        break;
                    case ATOM_PROTECTED:

                        // TODO - use different policy action? not sure.
                        // The POST sos/storage/stream/protected end-point is to be used when we want to protect the data, not for when the data is already protected
                        // However, it is also wrong if the storage on the other side stored the data as atom, rather than atomprotected.
                        // I think it is okay if the manifest has no keys, but that storage should know that it is protected data we are talking about.
                        break;
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

                    int numberReplicas = policyActions.numberOfDataReplicas(codomain, contentManifest.guid());
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

            policyActions.deleteData(manifest.guid(), codomain);
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            int numberReplicas = policyActions.numberOfDataReplicas(codomain, manifest.guid());
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
                throw new PolicyException("Policy. Granter " + granter.toMultiHash() +
                        " was unable to grant access to grantee " + grantee.toMultiHash() +
                        " for the content of version " + manifest.guid().toMultiHash());
            }
        }

        @Override
        public boolean satisfied(Manifest manifest) throws PolicyException {

            try {
                Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
                if (contentManifest.getType().equals(ManifestType.ATOM_PROTECTED)) {

                    SecureAtom secureAtom = (SecureAtom) contentManifest;
                    return secureAtom.keysRoles().containsKey(grantee);
                }
            } catch (ManifestNotFoundException e) {
                throw new PolicyException("Policy. Unable to check if whether the Granter " + granter.toMultiHash() +
                        " was able to grant access to the grantee " + grantee.toMultiHash() +
                        " for the content of version " + manifest.guid().toMultiHash());
            }

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
