package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.constants.Threads;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.actors.*;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.roles.RoleImpl;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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

    // The local storage allows data to be written locally to this node.
    // Note, however, that the local storage could the file system of this machine as well as a Dropbox service or an FTP server
    private LocalStorage localStorage;

    // This is a generic abstraction to interact with the Database of this node
    private Database database;

    // This scheduled service spawns a thread to check that the content of this node is within the specified restrictions.
    // If the restrictions are not satisfied, the background thread will remove any REMOVABLE content
    private ScheduledExecutorService cacheFlusherService;

    // Actors for this node
    private Agent agent;
    private Storage storage;
    private DataDiscoveryService dataDiscoveryService;
    private NodeDiscoveryService nodeDiscoveryService;
    private MetadataService metadataService;
    private ContextService contextService;
    private UsersRolesService usersRolesService;

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
    public DataDiscoveryService getDDS() {
        return dataDiscoveryService;
    }

    @Override
    public NodeDiscoveryService getNDS() {
        return nodeDiscoveryService;
    }

    @Override
    public MetadataService getMMS() {
        return metadataService;
    }

    @Override
    public ContextService getCMS() {
        return contextService;
    }

    @Override
    public UsersRolesService getRMS() {
        return usersRolesService;
    }

    @Override
    public void kill() {
        dataDiscoveryService.flush();
        storage.flush();
        cacheFlusherService.shutdown();

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
            nodeDiscoveryService.registerNode(node, true);
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

            nodeDiscoveryService.registerNode(this, false);
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
            Node localNode = new SOSNode(this);
            nodeDiscoveryService = new SOSNodeDiscoveryService(localNode, database);
            SOSURLProtocol.getInstance().register(localStorage, nodeDiscoveryService);
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
    }

    /**
     * Initialise all the actors for this node
     */
    private void initActors() {

        dataDiscoveryService = new SOSDataDiscoveryService(localStorage, nodeDiscoveryService);
        usersRolesService = new SOSUsersRolesService(localStorage); // TODO - will need to pass NDS to discover other roles

        storage = new SOSStorage(getNodeGUID(), localStorage, dataDiscoveryService);
        metadataService = new SOSMetadataService(new TikaMetadataEngine(), dataDiscoveryService);
        contextService = new SOSContextService(localStorage, dataDiscoveryService, nodeDiscoveryService, usersRolesService, storage);

        createDummyUserRole();

        agent = SOSAgent.instance(storage, dataDiscoveryService, metadataService, usersRolesService);
    }

    // FIXME - have better way to handle the default user and role
    private void createDummyUserRole() {

        try {
            User user = new UserImpl("simone");
            Role role = new RoleImpl(user, "student");

            usersRolesService.addUser(user);
            usersRolesService.addRole(role);
            usersRolesService.setActiveRole(role);

        } catch (SignatureException | ProtectionException | UserRolePersistException e) {
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
