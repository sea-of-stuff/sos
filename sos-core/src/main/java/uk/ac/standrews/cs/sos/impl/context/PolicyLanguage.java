package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.DDS;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.actors.SOSRMS;
import uk.ac.standrews.cs.sos.impl.manifests.SecureAtomManifest;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.protocol.tasks.DataReplication;
import uk.ac.standrews.cs.sos.protocol.tasks.ManifestReplication;

import java.io.IOException;
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

    private NDS nds;
    private DDS dds;

    private PolicyLanguage(NDS nds, DDS dds) {
        this.nds = nds;
        this.dds = dds;
    }

    private static PolicyLanguage instance;
    public static PolicyLanguage instance(NDS nds, DDS dds) {

        if (instance == null) {
            instance = new PolicyLanguage(nds, dds);
        }

        return instance;
    }

    public static PolicyLanguage instance() throws SOSException {

        if (instance != null) {
            return instance;
        }

        // Need to create instance with NDS and RMS first
        throw new SOSException("Unable to get Policy Language instance. Make an instance of it first!");
    }

    public void replicateManifest(Manifest manifest, Iterator<Node> nodes, int replicationFactor) {

        try {

            ManifestReplication replication = new ManifestReplication(manifest, nodes, replicationFactor, dds);
            TasksQueue.instance().performAsyncTask(replication);

        } catch (SOSProtocolException e) {
            e.printStackTrace();
        }
    }

    public void replicateData(Data data, Iterator<Node> nodes, int replicationFactor) {

        try {
            // FIXME - Remove that index. In fact, it should be part of the DDS
            DataReplication dataReplication = new DataReplication(data.getInputStream(), nodes, replicationFactor, null, nds, dds);
            TasksQueue.instance().performAsyncTask(dataReplication);

        } catch (SOSProtocolException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(IGUID guid, IGUID node) {

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

        return nds.getNode(guid);
    }

    public Set<Node> getNodes(Scope scope, NodeType type) {

        return nds.getNodes(type);
    }

    public Role getRole(IGUID guid) {

        return SOSRMS.instance().getRole(guid);
    }


}
