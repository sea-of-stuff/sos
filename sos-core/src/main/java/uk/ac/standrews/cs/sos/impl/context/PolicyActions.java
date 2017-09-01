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
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.VerifyData;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.services.Storage;
import uk.ac.standrews.cs.sos.services.UsersRolesService;

import java.io.IOException;

import static uk.ac.standrews.cs.sos.impl.services.SOSNodeDiscoveryService.NO_LIMIT;

/**
 * Utility methods accessible by the policies
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyActions {

    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;
    private UsersRolesService usersRolesService;
    private Storage storage;

    /**
     * Create a policy language utility object using the specified SOS services
     *
     * @param nodeDiscoveryService
     * @param dataDiscoveryService
     * @param usersRolesService
     * @param storage
     */
    public PolicyActions(NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService, UsersRolesService usersRolesService, Storage storage) {

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;
        this.usersRolesService = usersRolesService;
        this.storage = storage;
    }

    public void replicateManifest(Manifest manifest, NodesCollection nodes, int replicationFactor) throws PolicyException {

        try {
            dataDiscoveryService.addManifest(manifest, nodes, replicationFactor + 1 /* We increment the replication factory by one, because we want the manifest to leave this node */);
        } catch (ManifestPersistException e) {
            throw new PolicyException("Unable to replicate manifest");
        }
    }

    /**
     *
     * @param data
     * @param nodes
     * @param replicationFactor a value of 1 will make sure that the data is stored locally. A value of n, will replicate the data to n-1 storage nodes.
     * @throws PolicyException
     */
    public void replicateData(Data data, NodesCollection nodes, int replicationFactor) throws PolicyException {

        try {
            AtomBuilder atomBuilder = new AtomBuilder()
                    .setData(data)
                    .setReplicationNodes(nodes)
                    .setReplicationFactor(replicationFactor + 1 /* We increment the replication factory by one, because we want the data to leave this node */);

            storage.addAtom(atomBuilder);

        } catch (DataStorageException | ManifestPersistException e) {
            throw new PolicyException("Unable to replicate data");
        }
    }

    public void deleteData(IGUID guid, IGUID node) {
        // TODO - there is not such method yet as I have not thought of a way of removing data/content/assets yet
    }

    public boolean nodeHasManifest(IGUID node, IGUID guid) {

        // TODO - this will make a challenge/check/verify call to the node (see Storage)

        return false;
    }

    public boolean nodeHasData(IGUID nodeGUID, IGUID guid) {

        try {
            Node nodeToBeChallenged = nodeDiscoveryService.getNode(nodeGUID);
            VerifyData verifyData = new VerifyData(guid, storage.getAtomContent(guid), nodeToBeChallenged);

            TasksQueue.instance().performSyncTask(verifyData);
            return verifyData.isChallengePassed();

        } catch (NodeNotFoundException | GUIDGenerationException | AtomNotFoundException | IOException e) {

            return false;
        }

    }

    public int numberOfReplicas(NodesCollection codomain, IGUID guid) {

        int counter = 0;
        for(IGUID nodeRef:codomain.nodesRefs()) {

            if (nodeHasData(nodeRef, guid)) {
                counter++;
            }
        }

        return counter;
    }

    public Data getData(NodesCollection codomain, IGUID guid) throws AtomNotFoundException {

        // Check DDS, Storage restricting the request with the codomain
        // TODO Restrict by codomain
        return storage.getAtomContent(guid);
    }

    public Manifest getManifest(NodesCollection codomain, IGUID guid) throws ManifestNotFoundException {

        return dataDiscoveryService.getManifest(codomain, guid);
    }

    // Retrieve the manifest from local node
    public Manifest getContentManifest(Version version) throws ManifestNotFoundException {

        return dataDiscoveryService.getManifest(version.getContentGUID());
    }

    public Node getNode(IGUID guid) throws NodeNotFoundException {

        return nodeDiscoveryService.getNode(guid);
    }

    public NodesCollection getNodes(NodesCollection codomain, NodeType type) {

        return nodeDiscoveryService.filterNodesCollection(codomain, type, NO_LIMIT);
    }

    public Role getRole(IGUID guid) throws RoleNotFoundException {

        return usersRolesService.getRole(guid);
    }

    public void grantAccess(SecureAtom secureAtom, IGUID granter, IGUID grantee) throws RoleNotFoundException, ProtectionException {

        Role granterRole = usersRolesService.getRole(granter);
        Role granteeRole = usersRolesService.getRole(grantee);
        storage.grantAccess(secureAtom, granterRole, granteeRole);
    }




}
