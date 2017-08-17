package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.impl.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.impl.services.*;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    public static SettingsConfiguration.Settings settings;

    // The local storage allows data to be written locally to this node.
    // Note, however, that the local storage could the file system of this machine as well as a Dropbox service or an FTP server
    private LocalStorage localStorage;

    // This is a generic abstraction to interact with the Database of this node
    private Database database;

    // This scheduled service spawns a thread to check that the content of this node is within the specified restrictions.
    // If the restrictions are not satisfied, the background thread will remove any REMOVABLE content
    private ScheduledExecutorService cacheFlusherService;

    // Services for this node
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
     */
    public SOSLocalNode() throws SOSException {
        super(Builder.settings);

        SOSLocalNode.settings = Builder.settings;
        localStorage = Builder.localStorage;

        // Logo generated with: http://patorjk.com/software/taag/#p=display&f=Isometric3&t=SOS
        SOS_LOG.log(LEVEL.INFO,
                "\n" +
                        "      ___           ___           ___     \n" +
                        "     /  /\\         /  /\\         /  /\\    \n" +
                        "    /  /:/_       /  /::\\       /  /:/_   \n" +
                        "   /  /:/ /\\     /  /:/\\:\\     /  /:/ /\\  \n" +
                        "  /  /:/ /::\\   /  /:/  \\:\\   /  /:/ /::\\ \n" +
                        " /__/:/ /:/\\:\\ /__/:/ \\__\\:\\ /__/:/ /:/\\:\\\n" +
                        " \\  \\:\\/:/~/:/ \\  \\:\\ /  /:/ \\  \\:\\/:/~/:/\n" +
                        "  \\  \\::/ /:/   \\  \\:\\  /:/   \\  \\::/ /:/ \n" +
                        "   \\__\\/ /:/     \\  \\:\\/:/     \\__\\/ /:/  \n" +
                        "     /__/:/       \\  \\::/        /__/:/   \n" +
                        "     \\__\\/         \\__\\/         \\__\\/    \n");

        SOS_LOG.log(LEVEL.INFO, "Starting up node ");

        initDB();
        initNDS();
        loadBootstrapNodes(Builder.bootstrapNodes);
        registerNode();
        initServices();
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


    public void cleanup() throws DataStorageException {

        localStorage.destroy();
    }

    @Override
    public void kill() {
        dataDiscoveryService.flush();
        storage.flush();
        usersRolesService.flush();
        contextService.flush();

        if (cacheFlusherService != null)
            cacheFlusherService.shutdown();

        SOSAgent.destroy();
    }

    private void initDB() throws SOSException {
        try {
            String dbFilename = settings.getDatabase().getFilename();
            File file = localStorage.createFile(localStorage.getNodeDirectory(), dbFilename).toFile();

            database = new DatabaseImpl(file.getPath());
        } catch (DataStorageException | DatabaseException | IOException e) {
            throw new SOSException(e);
        }
    }

    /**
     * Load the bootstrap nodes specified in the configuration file into the local NDS.
     *
     * @param bootstrapNodes
     * @throws NodeRegistrationException
     */
    private void loadBootstrapNodes(List<SettingsConfiguration.Settings.NodeSettings> bootstrapNodes)
            throws NodeRegistrationException {

        for(Node node:bootstrapNodes) {
            nodeDiscoveryService.registerNode(node, true);
        }
    }

    /**
     * Register this node to the NDS network
     */
    private void registerNode() {

        try {
            int port = settings.getRest().getPort();
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.hostAddress = new InetSocketAddress(inetAddress, port);

            if (settings.getServices().getNds().isStartupRegistration()) {
                nodeDiscoveryService.registerNode(this, false);
            }

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
     * Initialise all the services for this node
     */
    private void initServices() throws ServiceException {

        dataDiscoveryService = new SOSDataDiscoveryService(localStorage, nodeDiscoveryService);
        usersRolesService = new SOSUsersRolesService(localStorage); // TODO - will need to pass NDS to discover other roles

        storage = new SOSStorage(settings.getServices().getStorage(), getNodeGUID(), localStorage, dataDiscoveryService, usersRolesService, nodeDiscoveryService);
        metadataService = new SOSMetadataService(new TikaMetadataEngine(), dataDiscoveryService);
        contextService = new SOSContextService(localStorage, dataDiscoveryService, nodeDiscoveryService, usersRolesService, storage);

        agent = SOSAgent.instance(storage, dataDiscoveryService, metadataService, usersRolesService);
    }

    /**
     * Launch a background scheduled process that checks the size of the local cache
     * and cleans it up if needed
     */
    private void cacheFlusher() {
        SOS_LOG.log(LEVEL.INFO, "Cache Flusher started");

        SettingsConfiguration.Settings.GlobalSettings.CacheFlusherSettings cacheFlusherSettings = SOSLocalNode.settings.getGlobal().getCacheFlusher();

        if (cacheFlusherSettings.isEnabled()) {

            SettingsConfiguration.Settings.ThreadSettings threadSettings = cacheFlusherSettings.getThread();

            CacheFlusher cacheFlusher = new CacheFlusher(localStorage);
            cacheFlusherService = Executors.newScheduledThreadPool(threadSettings.getPs());
            cacheFlusherService.scheduleAtFixedRate(cacheFlusher, threadSettings.getInitialDelay(), threadSettings.getPeriod(), CACHE_FLUSHER_TIME_UNIT);
        }
    }

    /**
     * This is the builder for the SOSLocalNode.
     */
    public static class Builder {
        private static SettingsConfiguration.Settings settings;
        private static LocalStorage localStorage;
        private static List<SettingsConfiguration.Settings.NodeSettings> bootstrapNodes;

        public Builder settings(SettingsConfiguration.Settings settings) {
            Builder.settings = settings;
            return this;
        }

        public Builder internalStorage(LocalStorage localStorage) {
            Builder.localStorage = localStorage;
            return this;
        }

        public Builder bootstrapNodes(List<SettingsConfiguration.Settings.NodeSettings> bootstrapNodes) {
            Builder.bootstrapNodes = bootstrapNodes;
            return this;
        }

        public SOSLocalNode build() throws SOSException {
            return new SOSLocalNode();
        }
    }

}
