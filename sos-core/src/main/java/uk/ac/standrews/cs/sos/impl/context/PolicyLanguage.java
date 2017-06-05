package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.DataDiscoveryService;
import uk.ac.standrews.cs.sos.actors.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.actors.Storage;
import uk.ac.standrews.cs.sos.actors.UsersRolesService;
import uk.ac.standrews.cs.sos.exceptions.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
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

    /**
     * Create a policy language utility object using the specified SOS actors
     *
     * @param nodeDiscoveryService
     * @param dataDiscoveryService
     * @param usersRolesService
     * @param storage
     */
    public PolicyLanguage(NodeDiscoveryService nodeDiscoveryService, DataDiscoveryService dataDiscoveryService, UsersRolesService usersRolesService, Storage storage) {

        this.nodeDiscoveryService = nodeDiscoveryService;
        this.dataDiscoveryService = dataDiscoveryService;
        this.usersRolesService = usersRolesService;
        this.storage = storage;
    }

    public void replicateManifest(Manifest manifest, NodesCollection nodes, int replicationFactor) throws PolicyException {

        try {
            dataDiscoveryService.addManifest(manifest, nodes, replicationFactor);
        } catch (ManifestPersistException e) {
            throw new PolicyException("Unable to replicate manifest");
        }
    }

    public void replicateData(Data data, NodesCollection nodes, int replicationFactor) {

        storage.addData(new AtomBuilder().setData(data), nodes, replicationFactor);
    }

    public void deleteData(IGUID guid, IGUID node) {
        // TODO - there is not such method yet as I have not thought of a way of removing data/content/assets yet
    }

    public boolean nodeHasData(IGUID node, IGUID guid) {

        // TODO - this will make a challenge/check/verify call to the node
        return false;
    }

    public int numberOfReplicas(NodesCollection codomain, IGUID guid) {

        // TODO - contact DDS/Storage?
        return -1;
    }

    public Data getData(NodesCollection codomain, IGUID guid) {

        // Check DDS, Storage restricting the request with the codomain
        return null;
    }

    public Manifest getManifest(NodesCollection codomain, IGUID guid) {

        // See getData()
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

        // TODO - replace encrypted data/manifest with the unencrypted one
    }

    public Node getNode(IGUID guid) throws NodeNotFoundException {

        return nodeDiscoveryService.getNode(guid);
    }

    public NodesCollection getNodes(NodesCollection codomain, NodeType type) {

        return nodeDiscoveryService.getNodes(codomain, type);
    }

    public Role getRole(IGUID guid) throws RoleNotFoundException {

        return usersRolesService.getRole(guid);
    }


}
