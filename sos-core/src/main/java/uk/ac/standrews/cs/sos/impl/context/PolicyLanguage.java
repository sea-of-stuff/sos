package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.actors.RMS;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.model.*;

import java.util.Set;

/**
 * Methods accessible by the policies
 *
 * What it is yet todo:
 * - calls to appropriate tasks
 * - indices for this node updated
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyLanguage {

    private static NDS nds;
    private static RMS rms;

    private PolicyLanguage(NDS nds, RMS rms) {
        this.nds = nds;
        this.rms = rms;
    }

    private static PolicyLanguage instance;
    public static PolicyLanguage instance(NDS nds, RMS rms) {

        if (instance == null) {
            instance = new PolicyLanguage(nds, rms);
        }

        return instance;
    }

    public static PolicyLanguage instance() throws SOSException {

        if (instance != null) {
            return instance;
        }

        // Need to create instance with NDS and RMS first
        throw new SOSException("Unable to get Policy Language instance");
    }

    public void replicateData(Data data, Set<IGUID> nodes, int replicationFactor) {

    }

    public void deleteData(IGUID guid, Set<IGUID> nodes) {

    }

    public boolean nodeHasData(IGUID node, IGUID guid) {

        return false;
    }

    public int numberOfReplicas(Scope scope, IGUID guid) {

        return -1;
    }

    public Data getData(Scope scope, IGUID guid) {

        return null;
    }

    public Manifest getManifest(Scope scope, IGUID guid) {

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

        return nds.getNode(guid); // TODO - restrict
    }

    public Set<Node> getNodes(Scope scope, int type /* node type */) {

        return null;
    }

    public Role getRole(IGUID guid) {

        return rms.getRole(guid);
    }


}
