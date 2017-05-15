package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.actors.*;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.node.directory.CP;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static uk.ac.standrews.cs.sos.constants.Internals.CACHE_FLUSHER_PERIOD;
import static uk.ac.standrews.cs.sos.constants.Internals.CACHE_FLUSHER_TIME_UNIT;

/**
 * This class represents the SOSNode of this machine.
 *
 * A SOSLocalNode may expose multiple SOS interfaces to the caller:
 * Agent, Storage, NDS, DDS, and MCS
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
    private Database database;

    private ScheduledExecutorService cacheFlusherService;

    // Actors for this node
    private Agent agent;
    private Storage storage;
    private DDS dds;
    private NDS nds;
    private MMS mms;
    private CMS cms;
    private RMS rms;

    // Each node will have its own log and it will be used to log errors as well
    // as useful information about the node itself.
    private SOS_LOG SOS_LOG = new SOS_LOG(getNodeGUID());

    /**
     * Construct the Node instance for this machine
     *
     * @throws SOSException
     * @throws GUIDGenerationException
     */
    public SOSLocalNode() throws SOSException, GUIDGenerationException {
        super(Builder.configuration);

        SOS_LOG.log(LEVEL.INFO, "Starting up node ");

        SOSConfiguration configuration = Builder.configuration;
        localStorage = Builder.localStorage;

        try {
            String dbFilename = configuration.getDBFilename();
            File file = localStorage.createFile(localStorage.getNodeDirectory(), dbFilename).toFile();

            database = new DatabaseImpl(file.getPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initNDS();
        loadBootstrapNodes(Builder.bootstrapNodes);
        registerNode(configuration);
        initActors();
        cacheFlusher();

        SOS_LOG.log(LEVEL.INFO, "Node started");
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
    public MMS getMMS() {
        return mms;
    }

    @Override
    public CMS getCMS() {
        return cms;
    }

    @Override
    public RMS getRMS() {
        return rms;
    }

    @Override
    public void kill() {
        dds.flush();
        storage.flush();
        cacheFlusherService.shutdown();

        try {
            CP.instance().kill();
        } catch (SQLException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to close the DB connection pool cleanly");
        }

        RequestsManager.getInstance().shutdown();
    }

    /**
     * Load the bootstrap nodes specified in the configuration file into the local NDS.
     *
     * @param bootstrapNodes
     * @throws NodeRegistrationException
     */
    private void loadBootstrapNodes(List<Node> bootstrapNodes)
            throws NodeRegistrationException {

        for(Node node:bootstrapNodes) {
            nds.registerNode(node, true);
        }
    }

    /**
     * Register this node to the NDS network
     *
     * @param configuration
     */
    private void registerNode(SOSConfiguration configuration) {

        try {
            int port = configuration.getNodePort();
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.hostAddress = new InetSocketAddress(inetAddress, port);

            nds.registerNode(this, false);
        } catch (UnknownHostException | NodeRegistrationException e) {
            SOS_LOG.log(LEVEL.ERROR, e.getMessage());
        }

    }

    /**
     * Initialise the local NDS actor
     *
     * @throws SOSException
     */
    private void initNDS() throws SOSException {
        try {
            SOSURLProtocol.getInstance().register(localStorage);
            nds = new SOSNDS(this, database);
            SOSURLProtocol.getInstance().setNDS(nds);
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
    }

    /**
     * Initialise all the actors for this node
     */
    private void initActors() {
        MetadataEngine metadataEngine = new TikaMetadataEngine();

        dds = new SOSDDS(localStorage, nds);
        storage = new SOSStorage(this, localStorage, nds, dds);
        mms = new SOSMMS(dds, metadataEngine);
        cms = new SOSCMS(localStorage, dds);
        rms = SOSRMS.instance();

        createDummyUserRole();

        agent = SOSAgent.instance(storage, dds, mms, rms);
    }

    // FIXME - have better way to handle the default user and role
    private void createDummyUserRole() {

        try {
            User user = new UserImpl("simone");
            Role role = new RoleImpl(user, "student");
            rms.addUser(user);
            rms.addRole(role);
            rms.setActive(role);

        } catch (KeyGenerationException | KeyLoadedException | CryptoException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch a background scheduled process that checks the size of the local cache
     * and cleans it up if needed
     */
    private void cacheFlusher() {
        SOS_LOG.log(LEVEL.INFO, "Cache Flusher started");

        CacheFlusher cacheFlusher = new CacheFlusher(localStorage);

        cacheFlusherService = Executors.newScheduledThreadPool(Threads.CACHE_FLUSHER_PS);
        cacheFlusherService.scheduleAtFixedRate(cacheFlusher, 0, CACHE_FLUSHER_PERIOD, CACHE_FLUSHER_TIME_UNIT);
    }

    /**
     * This is the builder for the SOSLocalNode.
     */
    public static class Builder {
        private static SOSConfiguration configuration;
        private static LocalStorage localStorage;
        private static List<Node> bootstrapNodes;

        public Builder configuration(SOSConfiguration configuration) {
            Builder.configuration = configuration;
            return this;
        }

        public Builder internalStorage(LocalStorage localStorage) {
            Builder.localStorage = localStorage;
            return this;
        }

        public Builder bootstrapNodes(List<Node> bootstrapNodes) {
            Builder.bootstrapNodes = bootstrapNodes;
            return this;
        }

        public SOSLocalNode build() throws SOSException, GUIDGenerationException {
            return new SOSLocalNode();
        }
    }

}
