package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.actors.SOSRMS;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
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

    private PolicyLanguage(NDS nds) {
        this.nds = nds;
    }

    private static PolicyLanguage instance;
    public static PolicyLanguage instance(NDS nds) {

        if (instance == null) {
            instance = new PolicyLanguage(nds);
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

    public void replicateManifest(Manifest manifest, Iterator<Node> nodes, int replicationFactor) {

        try {

            ManifestReplication replication = new ManifestReplication(manifest, nodes, replicationFactor, null);
            TasksQueue.instance().performAsyncTask(replication);

        } catch (SOSProtocolException e) {
            e.printStackTrace();
        }
    }

    public void replicateData(Data data, Iterator<Node> nodes, int replicationFactor) {

        try {
            DataReplication dataReplication = new DataReplication(data.getInputStream(), nodes, replicationFactor, null, null, null);
            TasksQueue.instance().performAsyncTask(dataReplication);

        } catch (SOSProtocolException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(IGUID guid, Iterator<Node> nodes) {

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

        return Collections.emptySet();
    }

    public Role getRole(IGUID guid) {

        return SOSRMS.instance().getRole(guid);
    }


}
