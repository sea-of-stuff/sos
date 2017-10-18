package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;

import java.io.IOException;
import java.util.Set;

/**
 * Common methods for policies
 *
 * TODO - methods to replicate metadata
 * TODO - notify nodes
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonPolicies {

    @SuppressWarnings("WeakerAccess")
    public static void apply_DoNothing() { }

    @SuppressWarnings("WeakerAccess")
    public static boolean satisfied_DoNothing() {
        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static void replicateManifest(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

        NodesCollection nodes = policyActions.getNodes(codomain, NodeType.DDS);
        policyActions.replicateManifest(manifest, nodes, factor);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean manifestIsReplicated(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

        int numberReplicas = policyActions.numberOfManifestReplicas(codomain, manifest.guid());
        return numberReplicas >= factor;
    }

    // TODO - have param for canPersist (e.g. replicate only to nodes that can persist data)
    @SuppressWarnings("WeakerAccess")
    public static void replicateData(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

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

    @SuppressWarnings("WeakerAccess")
    public static boolean dataIsReplicated(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

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

    @SuppressWarnings("WeakerAccess")
    public static void deleteData(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {

        policyActions.deleteData(manifest.guid(), codomain);
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean isDataDeleted(NodesCollection codomain, PolicyActions policyActions, Manifest manifest) throws PolicyException {

        int numberReplicas = policyActions.numberOfDataReplicas(codomain, manifest.guid());
        return numberReplicas == 0;
    }

    @SuppressWarnings("WeakerAccess")
    public static void grantAccess(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, IGUID granter, IGUID grantee) throws PolicyException {

        try {
            Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
            if (contentManifest.getType() == ManifestType.ATOM_PROTECTED ||
                    contentManifest.getType() == ManifestType.COMPOUND_PROTECTED) {

                policyActions.grantAccess((SecureManifest) contentManifest, granter, grantee);
            }

        } catch (RoleNotFoundException | ProtectionException | ManifestNotFoundException e) {
            throw new PolicyException("Policy. Granter " + granter.toMultiHash() +
                    " was unable to grant access to grantee " + grantee.toMultiHash() +
                    " for the content of version " + manifest.guid().toMultiHash());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean checkManifestIsProtected(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, IGUID granter, IGUID grantee) throws PolicyException {

        try {
            Manifest contentManifest = policyActions.getContentManifest((Version) manifest);
            if (contentManifest.getType() == ManifestType.ATOM_PROTECTED ||
                    contentManifest.getType() == ManifestType.COMPOUND_PROTECTED) {

                SecureManifest secureManifest = (SecureManifest) contentManifest;
                return secureManifest.keysRoles().containsKey(grantee);
            }

        } catch (ManifestNotFoundException e) {
            throw new PolicyException("Policy. Unable to check if whether the Granter " + granter.toMultiHash() +
                    " was able to grant access to the grantee " + grantee.toMultiHash() +
                    " for the content of version " + manifest.guid().toMultiHash());
        }

        return false;
    }

    @SuppressWarnings("WeakerAccess")
    public static void replicateAllVersions(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

        if (manifest.getType().equals(ManifestType.VERSION)) {

            Version version = (Version) manifest;
            IGUID invariant = version.invariant();

            Set<IGUID> versions = policyActions.getVersions(invariant);
            for(IGUID v:versions) {

                try {
                    Manifest m = policyActions.getManifest(v);
                    replicateManifest(codomain, policyActions, m, factor);

                } catch (ManifestNotFoundException | NodesCollectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static boolean allVersionAreReplicated(NodesCollection codomain, PolicyActions policyActions, Manifest manifest, int factor) throws PolicyException {

        if (manifest.getType().equals(ManifestType.VERSION)) {

            Version version = (Version) manifest;
            IGUID invariant = version.invariant();

            Set<IGUID> versions = policyActions.getVersions(invariant);
            for(IGUID v:versions) {

                try {
                    Manifest m = policyActions.getManifest(v);
                    if (!manifestIsReplicated(codomain, policyActions, m, factor)) {
                        return false;
                    }

                } catch (ManifestNotFoundException | NodesCollectionException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

}
