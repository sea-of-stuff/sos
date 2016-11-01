package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SOSImpl.*;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.MasterManifestManager;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataManager;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.interfaces.sos.*;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSProtocol;
import uk.ac.standrews.cs.sos.model.manifests.managers.ManifestsManagerImpl;
import uk.ac.standrews.cs.sos.model.metadata.MetadataManagerImpl;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.database.DatabaseType;
import uk.ac.standrews.cs.sos.node.database.SQLDatabase;
import uk.ac.standrews.cs.sos.storage.InternalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.util.List;

/**
 * This class represents the SOSNode of this machine.
 *
 * A SOSLocalNode may expose multiple SOS interfaces to the caller: Client, Storage
 *  NDS, DDS, and MCS
 *
 * A SOSLocalNode is created via a builder.
 * Example:
 * SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
 * SOSLocalNode localSOSNode = builder.configuration(configuration)
 *                                      .index(index)
 *                                      .internalStorage(internalStorage)
 *                                      .build();
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocalNode extends SOSNode implements LocalNode {

    private InternalStorage internalStorage;
    private PolicyManager policyManager;
    private NodeDatabase nodeDatabase;
    private Identity identity;
    private MasterManifestManager manifestsManager;
    private NodeManager nodeManager;
    private MetadataManager metadataManager;

    // Node roles
    // All roles will share storage, node manager, manifests manager, etc.
    private Client client;
    private Storage storage;
    private DDS dds;
    private NDS nds;
    private MCS mcs;

    // Each node will have its own log and it will be used to log errors as well
    // as useful information about the node itself.
    private SOS_LOG SOS_LOG = new SOS_LOG(getNodeGUID());

    public SOSLocalNode(Builder builder) throws SOSException, GUIDGenerationException {
        super(Builder.configuration);

        SOS_LOG.log(LEVEL.INFO, "Starting up node ");

        SOSConfiguration configuration = Builder.configuration;
        internalStorage = Builder.internalStorage;
        policyManager = Builder.policyManager;

        try {
            DatabaseType databaseType = configuration.getDBType();
            String databasePath = configuration.getDBPath(); // TODO - create db folder if necessary
            File dbDir = new File(databasePath);
            if (!dbDir.exists()) {
                dbDir.mkdir();
            }

            nodeDatabase = new SQLDatabase(databaseType, databasePath);
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        // TODO - register node with NDS

        initNodeManager();
        loadBootstrapNodes(Builder.bootstrapNodes);
        initManifestManager();
        initIdentity();
        initMetadataManager();

        initSOSInstances();

        try {
            SOSProtocol.Register(internalStorage, nodeManager);
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }

        garbageCollector();
        SOS_LOG.log(LEVEL.INFO, "Node initialised");
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public DDS getDDS() {
        return dds;
    }

    @Override
    public NDS getNDS() {
        return nds;
    }

    @Override
    public MCS getMCS() {
        return mcs;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public void kill() {
        manifestsManager.flush();

        RequestsManager.getInstance().shutdown();
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private void initNodeManager() throws SOSException {
        try {
            nodeManager = new NodeManager(this, nodeDatabase);
        } catch (NodeManagerException e) {
            throw new SOSException(e);
        }
    }

    private void loadBootstrapNodes(List<Node> bootstrapNodes)
            throws DatabaseConnectionException {

        for(Node node:bootstrapNodes) {
            nodeManager.addNode(node);
        }
    }

    private void initManifestManager() {
        ManifestPolicy manifestPolicy = policyManager.getManifestPolicy();
        manifestsManager = new ManifestsManagerImpl(manifestPolicy, internalStorage, nodeManager);
    }

    private void initIdentity() throws SOSException {
        try {
            identity = new IdentityImpl();
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

    }

    private void initMetadataManager() {
        MetadataEngine metadataEngine = new TikaMetadataEngine();
        MetadataPolicy metadataPolicy = policyManager.getMetadataPolicy();
        metadataManager = new MetadataManagerImpl(internalStorage, metadataEngine, metadataPolicy);
    }

    private void initSOSInstances() {
        if (isClient()) {
            SOS_LOG.log(LEVEL.INFO, "Creating a Client role");
            ReplicationPolicy replicationPolicy = policyManager.getReplicationPolicy();
            client = new SOSClient(this, nodeManager, internalStorage, manifestsManager,
                                    identity, replicationPolicy, metadataManager);
        }

        if (isStorage()) {
            SOS_LOG.log(LEVEL.INFO, "Creating a Storage role");
            storage = new SOSStorage(this, internalStorage, manifestsManager);
        }

        if (isDDS()) {
            SOS_LOG.log(LEVEL.INFO, "Creating a DDS role");
            dds = new SOSDDS(manifestsManager);
        }

        if (isNDS()) {
            SOS_LOG.log(LEVEL.INFO, "Creating a NDS role");
            nds = new SOSNDS(nodeManager);
        }

        if (isMCS()) {
            mcs = new SOSMCS();
        }
    }

    private void garbageCollector() {
        // check if garbage collector should be run (e.g. not enough space)
        // iterate over the atom manifests
        // check what atoms can be removed from the cache
        // clear cache
        SOS_LOG.log(LEVEL.INFO, "Garbage collector is not available yet");
    }

    /**
     * This is the builder for the SOSLocalNode.
     */
    public static class Builder {
        private static SOSConfiguration configuration;
        private static InternalStorage internalStorage;
        private static PolicyManager policyManager;
        private static List<Node> bootstrapNodes;

        public Builder configuration(SOSConfiguration configuration) {
            Builder.configuration = configuration;
            return this;
        }

        public Builder internalStorage(InternalStorage internalStorage) {
            Builder.internalStorage = internalStorage;
            return this;
        }

        public Builder policies(PolicyManager policyManager) {
            Builder.policyManager = policyManager;
            return this;
        }

        public Builder bootstrapNodes(List<Node> bootstrapNodes) {
            Builder.bootstrapNodes = bootstrapNodes;
            return this;
        }

        public SOSLocalNode build() throws SOSException, GUIDGenerationException {
            return new SOSLocalNode(this);
        }
    }

}
