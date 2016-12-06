package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.actors.*;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.node.NodesDatabase;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.model.metadata.MetadataDirectoryImpl;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.directory.database.DatabaseType;
import uk.ac.standrews.cs.sos.node.directory.database.SQLDatabase;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * This class represents the SOSNode of this machine.
 *
 * A SOSLocalNode may expose multiple SOS interfaces to the caller: Agent, Storage
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

    private LocalStorage localStorage;
    private PolicyManager policyManager;
    private NodesDatabase nodesDatabase;
    private Identity identity;
    private MetadataDirectory metadataDirectory;

    private ScheduledExecutorService garbageCollectorService;

    // Node roles
    // All roles will share storage, node directory, manifests directory, etc.
    private Agent agent;
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
        localStorage = Builder.localStorage;
        policyManager = Builder.policyManager;

        try {
            DatabaseType databaseType = configuration.getDBType();
            String databasePath = configuration.getDBPath();
            File dbDir = new File(databasePath);
            if (!dbDir.exists()) {
                new File(dbDir.getParent()).mkdir();
                dbDir.createNewFile();
            }

            nodesDatabase = new SQLDatabase(databaseType, databasePath);
        } catch (DatabaseException e) {
            throw new SOSException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO - register node with NDS (not this NDS)
        initNDS();

        loadBootstrapNodes(Builder.bootstrapNodes);
        initIdentity();
        initMetadataManager();

        initSOSInstances();
        garbageCollector();

        SOS_LOG.log(LEVEL.INFO, "Node initialised");
    }

    public Agent getAgent() {
        return agent;
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
        dds.flush();
        storage.flush();

        garbageCollectorService.shutdown();

        RequestsManager.getInstance().shutdown();
    }

    private void loadBootstrapNodes(List<Node> bootstrapNodes)
            throws NodeRegistrationException {

        for(Node node:bootstrapNodes) {
            nds.registerNode(node, true);
        }
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
        metadataDirectory = new MetadataDirectoryImpl(localStorage, metadataEngine, metadataPolicy); // TODO - do it in MCS
    }

    private void initNDS() throws SOSException {
        try {
            SOSURLProtocol.getInstance().register(localStorage);
            nds = new SOSNDS(this, nodesDatabase);
            SOSURLProtocol.getInstance().setNDS(nds);
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
    }

    private void initSOSInstances() {
        ReplicationPolicy replicationPolicy = policyManager.getReplicationPolicy();
        ManifestPolicy manifestPolicy = policyManager.getManifestPolicy();

        dds = new SOSDDS(localStorage, manifestPolicy, nds);
        storage = new SOSStorage(this, localStorage, replicationPolicy, nds, dds);
        mcs = new SOSMCS(metadataDirectory);
        agent = new SOSAgent(storage, dds, mcs, identity);
    }

    private void garbageCollector() {
        SOS_LOG.log(LEVEL.INFO, "Garbage collector started");

        GarbageCollector garbageCollector = new GarbageCollector(localStorage);

        garbageCollectorService = Executors.newScheduledThreadPool(1);
        garbageCollectorService.scheduleAtFixedRate(garbageCollector, 0, GarbageCollector.PERIOD, GarbageCollector.TIME_UNIT);

    }

    /**
     * This is the builder for the SOSLocalNode.
     */
    public static class Builder {
        private static SOSConfiguration configuration;
        private static LocalStorage localStorage;
        private static PolicyManager policyManager;
        private static List<Node> bootstrapNodes;

        public Builder configuration(SOSConfiguration configuration) {
            Builder.configuration = configuration;
            return this;
        }

        public Builder internalStorage(LocalStorage localStorage) {
            Builder.localStorage = localStorage;
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
