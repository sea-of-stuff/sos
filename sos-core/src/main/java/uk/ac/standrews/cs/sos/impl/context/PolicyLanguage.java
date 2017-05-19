package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.DataDiscoveryService;
import uk.ac.standrews.cs.sos.actors.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.actors.Storage;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;

/**
 * Utility methods accessible by the policies
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyLanguage {

    private NodeDiscoveryService nodeDiscoveryService;
    private DataDiscoveryService dataDiscoveryService;
    private UsersRolesService usersRolesService;
    private Storage storage;

    public PolicyLanguage(NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService, UsersRolesService usersRolesService, Storage storage) {

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;
        this.usersRolesService = usersRolesService;
        this.storage = storage;
    }

    public void replicateManifest(Manifest manifest, NodesCollection nodes, int replicationFactor) {

        dataDiscoveryService.addManifest(manifest, nodes, replicationFactor);
    }

    public void replicateData(Data data, NodesCollection nodes, int replicationFactor) {

        storage.addData(new AtomBuilder().setData(data), nodes, replicationFactor);
    }

    public void deleteData(IGUID guid, IGUID node) {

    }

    public boolean nodeHasData(IGUID node, IGUID guid) {

        return false;
    }

    public int numberOfReplicas(NodesCollection codomain, IGUID guid) {

        return -1;
    }

    public Data getData(NodesCollection codomain, IGUID guid) {

        return null;
    }

    public Manifest getManifest(NodesCollection codomain, IGUID guid) {

        return null;
    }

    /**
     * Encrypt the given data using the role
     *
     * @param atom
     * @param role
     */
    public SecureAtomManifest protect(Atom atom, Role role) {

        // TODO - encrypt data and generate secure atom manifest
        return null;
    }

    /**
     * Decrypt the secure manifest using the given role
     *
     * The role must have the secret key to decrypt the secure manifest
     *
     * @param secureManifest
     * @param role
     */
    public void unprotect(SecureManifest secureManifest, Role role) {

    }

    public Node getNode(IGUID guid) throws NodeNotFoundException {

        return nodeDiscoveryService.getNode(guid);
    }

    public NodesCollection getNodes(NodesCollection codomain, NodeType type) {

        return null;
        // return nodeDiscoveryService.getNodes(type);
    }

    public Role getRole(IGUID guid) {

        return usersRolesService.getRole(guid);
    }


}
