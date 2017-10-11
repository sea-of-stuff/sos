package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.DataChallenge;
import uk.ac.standrews.cs.sos.impl.protocol.tasks.ManifestChallenge;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.services.UsersRolesService;

import java.io.IOException;
import java.util.Set;

import static uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService.NO_LIMIT;

/**
 * Utility methods accessible by the policies.
 *
 * The policy actions ARE NOT policies. These are just common methods that policies can make use of.
 * That means that policies do not have to necessarily use these methods, even though it is suggested to do so.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyActions {

    // Handles for the node services.
    private NodeDiscoveryService nodeDiscoveryService;
    private ManifestsDataService manifestsDataService;
    private UsersRolesService usersRolesService;
    private Storage storage;

    /**
     * Create a policy language utility object using the specified SOS services
     *
     * @param nodeDiscoveryService
     * @param manifestsDataService
     * @param usersRolesService
     * @param storage
     */
    public PolicyActions(NodeDiscoveryService nodeDiscoveryService, ManifestsDataService manifestsDataService, UsersRolesService usersRolesService, Storage storage) {

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.manifestsDataService = manifestsDataService;
        this.usersRolesService = usersRolesService;
        this.storage = storage;
    }

    /**
     * Replicate a manifest to a collection of nodes while attempting to satisfy the specified replication factor.
     *
     * @param manifest to be replicated
     * @param codomain where to replicate the manifests
     * @param replicationFactor a value of 1 will replicate the manifest to 1 node
     * @throws PolicyException if the manifest could not be replicated
     */
    void replicateManifest(Manifest manifest, NodesCollection codomain, int replicationFactor) throws PolicyException {

        try {
            manifestsDataService.addManifest(manifest, codomain, replicationFactor + 1 /* We increment the replication factory by one, because we want the manifest to leave this node */);
        } catch (ManifestPersistException e) {
            throw new PolicyException("Unable to replicate manifest");
        }

        // TODO - shallow vs deep
    }

    /**
     * TODO  - delegate replication to other nodes
     *
     * Replicate data to a collection of nodes while attempting to satisfy the specified replication factor.
     *
     * @param data to be replicated
     * @param codomain where to replicate the data
     * @param replicationFactor a value of 1 will replicate the data to 1 node
     * @throws PolicyException if the data could not be replicated
     */
    void replicateData(Data data, NodesCollection codomain, int replicationFactor) throws PolicyException {

        // FIXME - differentiate between clear data and protected data
        try {
            AtomBuilder atomBuilder = new AtomBuilder()
                    .setData(data)
                    .setReplicationNodes(codomain)
                    .setReplicationFactor(replicationFactor + 1 /* We increment the replication factory by one, because we want the data to leave this node */);

            storage.addAtom(atomBuilder);

        } catch (DataStorageException | ManifestPersistException e) {
            throw new PolicyException("Unable to replicate data");
        }
    }

    void updateVersion(IGUID guid, NodesCollection codomain) {
        // TODO
    }

    /**
     * Delete data from a collection of nodes.
     *
     * @param guid of the data to delete
     * @param codomain where to delete the data
     */
    void deleteData(IGUID guid, NodesCollection codomain) {

        // This operation is currently not supported by the SOS
        throw new UnsupportedOperationException();
    }

    /**
     * Check if a node has the manifest with the matching guid
     *
     * TODO - shallow vs deep challenge
     * a deep challenge will also challenge the content referenced by the manifest
     *
     * @param nodeGUID of the node
     * @param guid of the manifest
     * @return true if the node has the manifest
     */
    private boolean nodeHasManifest(IGUID nodeGUID, IGUID guid) {

        try {
            Node nodeToBeChallenged = nodeDiscoveryService.getNode(nodeGUID);

            Manifest manifest = getManifest(guid);
            ManifestChallenge manifestChallenge = new ManifestChallenge(guid, manifest, nodeToBeChallenged);

            TasksQueue.instance().performSyncTask(manifestChallenge);
            return manifestChallenge.isChallengePassed();

        } catch (ManifestNotFoundException | NodesCollectionException | NodeNotFoundException | GUIDGenerationException | IOException e) {

            return false;
        }

    }

    /**
     * Check the number of manifest replicas within a codomain
     *
     * @param codomain nodes to check
     * @param guid of the manifest
     * @return number of replicas
     */
    int numberOfManifestReplicas(NodesCollection codomain, IGUID guid) {

        int counter = 0;
        for(IGUID nodeRef:codomain.nodesRefs()) {

            if (nodeHasManifest(nodeRef, guid)) {
                counter++;

                // TODO - make sure that the DDS is updated with locations
            }
        }

        return counter;
    }

    /**
     * Check if a node has the data with the matching guid
     *
     * The node is challenged
     *
     * @param nodeGUID of the node
     * @param guid of the data
     * @return true if the node has the data
     */
    private boolean nodeHasData(IGUID nodeGUID, IGUID guid) {

        try {
            Node nodeToBeChallenged = nodeDiscoveryService.getNode(nodeGUID);
            DataChallenge dataChallenge = new DataChallenge(guid, storage.getAtomContent(guid), nodeToBeChallenged);

            TasksQueue.instance().performSyncTask(dataChallenge);
            return dataChallenge.isChallengePassed();

        } catch (NodeNotFoundException | GUIDGenerationException | AtomNotFoundException | IOException e) {

            return false;
        }

    }

    /**
     * Check the number of replicas for the data within a codomain.
     *
     * @param codomain nodes to check
     * @param guid of the data
     * @return number of replicas
     */
    int numberOfDataReplicas(NodesCollection codomain, IGUID guid) {

        int counter = 0;
        for(IGUID nodeRef:codomain.nodesRefs()) {

            if (nodeHasData(nodeRef, guid)) {
                counter++;

                // TODO - make sure that the storage locationsindex is updated
            }
        }

        return counter;
    }

    /**
     * Grant access to a secure atom
     *
     * @param secureAtom atom for which access must be granted
     * @param granter the role granting the access
     * @param grantee the role receiving the access
     * @throws RoleNotFoundException if one of the two roles cannot be found
     * @throws ProtectionException if the access could not be granted
     */
    void grantAccess(SecureAtom secureAtom, IGUID granter, IGUID grantee) throws RoleNotFoundException, ProtectionException {

        Role granterRole = usersRolesService.getRole(granter);
        Role granteeRole = usersRolesService.getRole(grantee);
        storage.grantAccess(secureAtom, granterRole, granteeRole);
    }

    ////////////////////////////////
    // NOTE - Utility methods that should be moved outside of the policy actions
    ////////////////////////////////


    /**
     * Get the data of an atom from a codomain.
     *
     * @param guid
     * @return
     * @throws AtomNotFoundException
     */
    Data getData(IGUID guid) throws AtomNotFoundException {

        // Check DDS, Storage restricting the request with the codomain
        // TODO Restrict by codomain
        return storage.getAtomContent(guid);
    }

    /**
     * TODO - this is not a policy action. Move it to another class
     *
     * Get the manifest from a codomain
     *
     * @param guid
     * @return
     * @throws ManifestNotFoundException
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException, NodesCollectionException {

        NodesCollection domain = new NodesCollectionImpl(NodesCollectionType.LOCAL);
        return manifestsDataService.getManifest(domain, guid);
    }

    /**
     * TODO - this is not a policy action. Move it to another class
     *
     * Get the manifest of a version's content.
     *
     * @param version
     * @return
     * @throws ManifestNotFoundException
     */
    // Retrieve the manifest from local node
    Manifest getContentManifest(Version version) throws ManifestNotFoundException {

        return manifestsDataService.getManifest(version.content());
    }

    // TODO - this is not a policy action. Move it to another class
    Set<IGUID> getVersions(IGUID invariant) {

        return manifestsDataService.getVersions(invariant);
    }

    /**
     * TODO - this is not a policy action. Move it to another class
     *
     * Get the node with the specified guid
     * @param guid
     * @return
     * @throws NodeNotFoundException
     */
    Node getNode(IGUID guid) throws NodeNotFoundException {

        return nodeDiscoveryService.getNode(guid);
    }

    /**
     * TODO - this is not a policy action. Move it to another class
     *
     * Filter the codomain by type
     *
     * @param codomain
     * @param type
     * @return
     */
    NodesCollection getNodes(NodesCollection codomain, NodeType type) {

        return nodeDiscoveryService.filterNodesCollection(codomain, type, NO_LIMIT);
    }

    /**
     * Get the role with the specified guid
     *
     * @param guid
     * @return
     * @throws RoleNotFoundException
     */
    Role getRole(IGUID guid) throws RoleNotFoundException {

        return usersRolesService.getRole(guid);
    }

    /**
     * Get the user with the specified guid
     *
     * @param guid
     * @return
     * @throws UserNotFoundException
     */
    User getUser(IGUID guid) throws UserNotFoundException {

        return usersRolesService.getUser(guid);
    }

}
