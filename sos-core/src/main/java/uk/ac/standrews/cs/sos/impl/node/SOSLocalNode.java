package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.node.NodeRegistrationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.impl.database.DatabaseFactory;
import uk.ac.standrews.cs.sos.impl.database.DatabaseType;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.impl.services.*;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static uk.ac.standrews.cs.sos.constants.Internals.NODE_MAINTAINER_TIME_UNIT;

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

    // The SOSLocalNode will use this private key to digitally sign messages
    // that the receivers can then verify using the known digital signature certificate (public key) of this node.
    private PrivateKey signaturePrivateKey;

    public static SettingsConfiguration.Settings settings;

    // The local storage allows data to be written locally to this node.
    // Note, however, that the local storage could the file system of this machine as well as a Dropbox service or an FTP server
    private LocalStorage localStorage;

    // This is a generic abstraction to interact with the Database of this node
    private NodesDatabase nodesDatabase;

    private NodeMaintainer nodeMaintainer;
    // This scheduled service spawns a thread to check that the content of this node is within the specified restrictions.
    // If the restrictions are not satisfied, the background thread will remove any REMOVABLE content
    private ScheduledExecutorService nodeMaintainerService;

    // Services for this node
    private Agent agent;
    private StorageService storageService;
    private ManifestsDataService manifestsDataService;
    private NodeDiscoveryService nodeDiscoveryService;
    private MetadataService metadataService;
    private ContextService contextService;
    private UsersRolesService usersRolesService;

    // Each node will have its own log and it will be used to log errors as well
    // as useful information about the node itself.
    private SOS_LOG SOS_LOG;

    /**
     * Construct the Node instance for this machine
     *
     * @throws SOSException
     */
    public SOSLocalNode() throws SOSException {
        super(Builder.settings);

        SOSLocalNode.settings = Builder.settings;
        localStorage = Builder.localStorage;

        manageSignatureKeys();
        manageNodeGUID();
        SOS_LOG = new SOS_LOG(guid());

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
                        "     \\__\\/         \\__\\/         \\__\\/    \n" +
                        "\n\n" +
                        " --------------------------------------------------------\n" +
                        "| Warning/Notes:                                         |\n" +
                        "|   This is a prototype version of the SOS.              |\n" +
                        "|   Use this software at your own discretion!            |\n" +
                        "|   There are still bugs and missing features.           |\n" +
                        "|   Visit https://github.com/stacs-srg/sos for more info |\n" +
                        " --------------------------------------------------------" +
                        "\n\n" +
                        "Starting up Node with GUID: " + this.guid().toMultiHash() + "\n");

        initDB();
        initBasicServices();
        loadBootstrapNodes();
        registerNode();
        initServices();
        initNodeMaintainer();

        SOS_LOG.log(LEVEL.INFO, "Node started");
    }

    public Agent getAgent() {
        return agent;
    }

    @Override
    public StorageService getStorageService() {
        return storageService;
    }

    @Override
    public ManifestsDataService getMDS() {
        return manifestsDataService;
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
    public UsersRolesService getUSRO() {
        return usersRolesService;
    }

    // THIS METHOD IS GOING TO BE USED BY EXPERIMENTS ONLY.
    // THIS METHOD CLEANS UP EVERTHING...SO USE IT WITH PRECAUTION
    public void cleanup() throws DataStorageException {

        SOS_LOG.log(LEVEL.WARN, "Cleaning up SOS NODE");
        localStorage.destroy();
        SOS_LOG.log(LEVEL.WARN, "SOS NODE cleaned up");
    }

    @Override
    public void kill() {
        SOS_LOG.log(LEVEL.WARN, "Killing SOS NODE");

        if (nodeMaintainer != null) {
            nodeMaintainer.flush();
            nodeMaintainer.shutdown();
        }

        if (nodeMaintainerService != null) {
            nodeMaintainerService.shutdown();
        }

        if (agent != null) {
            agent.shutdown();
        }

        if (contextService != null) {
            contextService.shutdown();
        }

        if (usersRolesService != null) {
            usersRolesService.shutdown();
        }

        if (metadataService != null) {
            metadataService.shutdown();
        }

        if (storageService != null) {
            storageService.shutdown();
        }

        if (manifestsDataService != null) {
            manifestsDataService.shutdown();
        }

        if (nodeDiscoveryService != null) {
            nodeDiscoveryService.shutdown();
        }

        DatabaseFactory.kill();
        SOSAgent.destroy();
        SOS_LOG.log(LEVEL.WARN, "SOS NODE killed");
    }

    public String sign(String message) throws CryptoException {

        return DigitalSignature.sign64(signaturePrivateKey, message);
    }

    /**
     *
     * Attempt to load the private key and the certificate for the digital signature.
     * If keys cannot be loaded, then generate them and save to disk
     *
     * @throws SignatureException if an error occurred while managing the keys
     * @throws DataStorageException if the keys could not be accessed and/or stored
     */
    private void manageSignatureKeys() throws SignatureException, DataStorageException {

        IDirectory nodeDirectory = localStorage.getNodeDirectory();

        try {
            IFile publicKeyFile = localStorage.createFile(nodeDirectory, "id_rsa" + DigitalSignature.CERTIFICATE_EXTENSION);
            if (signatureCertificate == null && publicKeyFile.exists()) {
                signatureCertificate = DigitalSignature.getCertificate(publicKeyFile.toFile().toPath());
            }

            IFile privateKeyFile = localStorage.createFile(nodeDirectory, "id_rsa" + DigitalSignature.PRIVATE_KEY_EXTENSION);
            if (signaturePrivateKey == null && privateKeyFile.exists()) {
                signaturePrivateKey = DigitalSignature.getPrivateKey(privateKeyFile.toFile().toPath());
            }

            if (signatureCertificate == null && signaturePrivateKey == null) {

                KeyPair keys = DigitalSignature.generateKeys();
                signatureCertificate = keys.getPublic();
                signaturePrivateKey = keys.getPrivate();

                DigitalSignature.persist(keys,
                        Paths.get(nodeDirectory.getPathname() + "id_rsa"),
                        Paths.get(nodeDirectory.getPathname() + "id_rsa"));
            }

        } catch (CryptoException | IOException e) {
            throw new SignatureException(e);
        }
    }

    private void manageNodeGUID() throws SOSException {

        try (InputStream content = contentToHash()){

            this.nodeGUID = GUIDFactory.generateGUID(content);
            this.DB_nodeid = nodeGUID.toMultiHash();

            settings.setGuid(nodeGUID.toMultiHash());
        } catch (GUIDGenerationException | IOException e) {
            throw new SOSException("Unable to generate GUID for SOSLocalNode");
        }

    }

    private void initDB() throws SOSException {
        try {
            String dbFilename = settings.getDatabase().getFilename();
            File file = localStorage.createFile(localStorage.getNodeDirectory(), dbFilename).toFile();

            DatabaseFactory.initInstance(file.getPath());
            nodesDatabase = (NodesDatabase) DatabaseFactory.instance().getDatabase(DatabaseType.NODES);
        } catch (DataStorageException | DatabaseException | IOException e) {
            throw new SOSException(e);
        }
    }

    /**
     * Load the bootstrap nodes specified in the configuration file into the local NDS.
     *
     * @throws NodeRegistrationException if the node could not be registered
     */
    private void loadBootstrapNodes() throws NodeRegistrationException {

        if (!settings.getServices().getNds().isBootstrap()) return;

        for(Node node:settings.getBootstrapNodes()) {

            try {
                String nodeInfo = "";
                try {
                    // Get info by GUID
                    nodeInfo = nodeDiscoveryService.infoNode(node.guid());
                } catch (NodeNotFoundException e) {
                    SOS_LOG.log(LEVEL.ERROR, "Unable to bootstrap node with GUID: " + node.guid().toMultiHash());

                    try {
                        nodeInfo = nodeDiscoveryService.infoNode(node);
                    } catch (NodeNotFoundException e1) {
                        SOS_LOG.log(LEVEL.ERROR, "Unable to bootstrap node with address: " + node.getHostAddress().toString());
                    }
                }

                Node retrievedNode = JSONHelper.JsonObjMapper().readValue(nodeInfo, SOSNode.class);
                nodeDiscoveryService.registerNode(retrievedNode, true);

            } catch (IOException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to register node with GUID " + node.guid().toMultiHash() + " and address " + node.getHostAddress().toString());
            }
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
    private void initBasicServices() throws SOSException {

        // Here we build a circular dependency between the NDS and the MDS, but it is necessary to handle nodes as first class entities
        Node localNode = new SOSNode(this);
        nodeDiscoveryService = new SOSNodeDiscoveryService(localNode, nodesDatabase);
        SOSURLProtocol.getInstance().register(localStorage, nodeDiscoveryService);

        manifestsDataService = new SOSManifestsDataService(settings.getServices().getDds(), localStorage, nodeDiscoveryService);
        nodeDiscoveryService.setMDS(manifestsDataService);
    }

    /**
     * Initialise all the remaining services for this node
     *
     * The services use the localStorage to persist their indices/caches to disk
     * The manifestsDataService allow entities to be handles as first class entities in the SOS and in a consistent manner
     */
    private void initServices() throws ServiceException {

        usersRolesService = new SOSUsersRolesService(localStorage, manifestsDataService);

        storageService = new SOSStorageService(settings.getServices().getStorage(), guid(), localStorage, manifestsDataService, nodeDiscoveryService);
        metadataService = new SOSMetadataService(new TikaMetadataEngine(), manifestsDataService);

        CommonUtilities commonUtilities = new CommonUtilities(nodeDiscoveryService, manifestsDataService, usersRolesService, storageService);
        contextService = new SOSContextService(localStorage, manifestsDataService, commonUtilities);

        agent = SOSAgent.instance(storageService, manifestsDataService, metadataService, usersRolesService);
    }

    /**
     * Launch a background scheduled process that checks the size of the local cache
     * and cleans it up if needed
     */
    private void initNodeMaintainer() {

        nodeMaintainer = new NodeMaintainer(localStorage, manifestsDataService, storageService, usersRolesService, contextService);

        SettingsConfiguration.Settings.GlobalSettings.NodeMaintainerSettings nodeMaintainerSettings = SOSLocalNode.settings.getGlobal().getNodeMaintainer();
        if (nodeMaintainerSettings.isEnabled()) {
            SOS_LOG.log(LEVEL.INFO, "Node Maintainer is ENABLED");

            SettingsConfiguration.Settings.ThreadSettings threadSettings = nodeMaintainerSettings.getThread();
            nodeMaintainerService = Executors.newScheduledThreadPool(threadSettings.getPs());
            nodeMaintainerService.scheduleAtFixedRate(nodeMaintainer, threadSettings.getInitialDelay(), threadSettings.getPeriod(), NODE_MAINTAINER_TIME_UNIT);
        }
    }

    /**
     * This is the builder for the SOSLocalNode.
     */
    public static class Builder {
        private static SettingsConfiguration.Settings settings;
        private static LocalStorage localStorage;

        public Builder settings(SettingsConfiguration.Settings settings) {
            Builder.settings = settings;
            return this;
        }

        public Builder internalStorage(LocalStorage localStorage) {
            Builder.localStorage = localStorage;
            return this;
        }

        public SOSLocalNode build() throws SOSException {
            return new SOSLocalNode();
        }
    }

}
