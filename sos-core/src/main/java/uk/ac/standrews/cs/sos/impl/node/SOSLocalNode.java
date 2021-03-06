/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.node;

import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
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
import uk.ac.standrews.cs.sos.impl.protocol.TasksQueue;
import uk.ac.standrews.cs.sos.impl.services.*;
import uk.ac.standrews.cs.sos.interfaces.database.NodesDatabase;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.services.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.DigitalSignature;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static uk.ac.standrews.cs.sos.constants.Internals.*;

/**
 * This class represents the SOSNode of this machine.
 *
 * A SOSLocalNode may expose multiple SOS interfaces to the caller:
 * Agent, Storage, NDS, MDS, and MCS
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
    private PrivateKey d_privateKey;

    public static SettingsConfiguration.Settings settings;

    // The local storage allows data to be written locally to this node.
    // Note, however, that the local storage could be the file system of this machine as well as a Dropbox service or an FTP server
    private LocalStorage localStorage;

    // This is a generic abstraction to interact with the Database of this node
    private NodesDatabase nodesDatabase;

    private NodeMaintainer nodeMaintainer;
    // This scheduled service spawns a thread to check that the content of this node is within the specified restrictions.
    // If the restrictions are not satisfied, the background thread will remove any REMOVABLE content
    private ScheduledExecutorService nodeMaintainerService;

    // Services for this node
    private boolean restEnabled = true;
    private Agent agent;
    private StorageService storageService;
    private ManifestsDataService manifestsDataService;
    private NodeDiscoveryService nodeDiscoveryService;
    private MetadataService metadataService;
    private ContextService contextService;
    private UsersRolesService usersRolesService;

    /**
     * Construct the Node instance for this machine
     *
     * @throws SOSException if this node could not be created successfully.
     */
    public SOSLocalNode() throws SOSException {
        super(Builder.settings);

        SOSLocalNode.settings = Builder.settings;
        localStorage = Builder.localStorage;

        manageSignatureKeys();
        manageNodeGUID();
        initLog();

        // Logo generated with: http://patorjk.com/software/taag/#p=display&f=Isometric3&t=SOS
        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.INFO,
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
                        "Starting up Node with GUID: " + this.guid().toMultiHash() + "\n" +
                        "Address: " + this.getHostAddress().toString() + "\n");

        initRequestManager();
        initDB();
        initBasicServices();
        loadBootstrapNodes();
        registerNode();
        initServices();
        initNodeMaintainer();

        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.INFO, "Node started");
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

        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "Cleaning up SOS NODE");
        localStorage.destroy();
        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "SOS NODE cleaned up");
    }

    @Override
    public void kill(boolean flush) {
        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "Killing SOS NODE");

        if (nodeMaintainer != null) {
            if (flush) nodeMaintainer.flush();
            nodeMaintainer.shutdown();
        }

        if (nodeMaintainerService != null) {
            nodeMaintainerService.shutdown();
        }

        TasksQueue.instance().shutdown();

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

        if (RequestsManager.getInstance() != null) {
            RequestsManager.getInstance().shutdown();
        }

        DatabaseFactory.kill();
        SOSAgent.destroy();

        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "SOS NODE killed");
    }

    @Override
    public LocalStorage getLocalStorage() {

        if (settings.getServices().getExperiment().isExposed()) {

            return localStorage;
        }

        return null;
    }

    public String sign(String message) throws CryptoException {

        return DigitalSignature.sign64(d_privateKey, message);
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
            if (d_publicKey == null && publicKeyFile.exists()) {
                d_publicKey = DigitalSignature.getCertificate(publicKeyFile.getPath());
            }

            IFile privateKeyFile = localStorage.createFile(nodeDirectory, "id_rsa" + DigitalSignature.PRIVATE_KEY_EXTENSION);
            if (d_privateKey == null && privateKeyFile.exists()) {
                d_privateKey = DigitalSignature.getPrivateKey(privateKeyFile.getPath());
            }

            if (d_publicKey == null && d_privateKey == null) {

                KeyPair keys = DigitalSignature.generateKeys();
                d_publicKey = keys.getPublic();
                d_privateKey = keys.getPrivate();

                DigitalSignature.persist(keys,
                        Paths.get(nodeDirectory.getPathname() + "id_rsa"),
                        Paths.get(nodeDirectory.getPathname() + "id_rsa"));
            }

        } catch (CryptoException e) {
            throw new SignatureException(e);
        }
    }

    private void manageNodeGUID() throws SOSException {

        try (InputStream content = contentToHash()){

            this.nodeGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, content);
            this.setDB_NODEID(nodeGUID.toMultiHash());
            settings.setGuid(nodeGUID.toMultiHash());
            
        } catch (GUIDGenerationException | IOException e) {
            throw new SOSException("Unable to generate GUID for SOSLocalNode");
        }

    }

    private void initLog() {
        // Each node will have its own log and it will be used to log errors as well
        // as useful information about the node itself.
        new SOS_LOG(guid());
    }

    private void initRequestManager() {

        // Pass the private key to the request manager, so that requests can be signed by the node
        RequestsManager.init(d_privateKey);
    }

    private void initDB() throws SOSException {
        try {
            IFile dbFile = localStorage.createFile(localStorage.getNodeDirectory(), DB_FILE);
            DatabaseFactory.initInstance(dbFile);
            nodesDatabase = (NodesDatabase) DatabaseFactory.instance().getDatabase(DatabaseType.NODES);
        } catch (DataStorageException | DatabaseException e) {
            throw new SOSException(e);
        }
    }

    /**
     * Load the bootstrap nodes specified in the configuration file into the local NDS.
     *
     * @throws NodeRegistrationException if the node could not be registered
     */
    public void loadBootstrapNodes() throws NodeRegistrationException {

        if (!settings.getServices().getNds().isBootstrap()) return;

        for(Node node:settings.getBootstrapNodes()) {

            IGUID boostrapNodeGUID = node.guid();
            try {
                String nodeInfo = "";
                try {
                    // Get info by GUID
                    nodeInfo = nodeDiscoveryService.infoNode(boostrapNodeGUID);
                } catch (NodeNotFoundException e) {
                    uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "Unable to bootstrap node with GUID: " + boostrapNodeGUID.toMultiHash() + " -- Will try to contact it with known IP:port into");

                    try {
                        nodeInfo = nodeDiscoveryService.infoNode(node);
                    } catch (NodeNotFoundException e1) {
                        uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.ERROR, "Unable to bootstrap node with address: " + node.getHostAddress().toString());
                    }
                }

                if (nodeInfo != null && !nodeInfo.isEmpty()) {
                    Node retrievedNode = JSONHelper.jsonObjMapper().readValue(nodeInfo, SOSNode.class);
                    nodeDiscoveryService.registerNode(retrievedNode, true);
                    uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.INFO, "Registered bootstrap node with GUID: " + boostrapNodeGUID.toMultiHash());
                } else {
                    uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.WARN, "Unknown nodeInfo for node with GUID: " + boostrapNodeGUID.toMultiHash() +
                            " -- Node could not be registered to node service");
                }

            } catch (IOException e) {
                uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.ERROR, "Unable to register node with GUID " + boostrapNodeGUID.toMultiHash() +
                        " and address " + node.getHostAddress().toString());
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
            uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.ERROR, e.getMessage());
        }

    }

    /**
     * Initialise the local NDS actor
     *
     * @throws SOSException if some of the services could not be instantiated.
     */
    private void initBasicServices() throws SOSException {

        // Here we build a circular dependency between the NDS and the MDS, but it is necessary to handle nodes as first class entities
        Node localNode = new SOSNode(this);
        nodeDiscoveryService = new SOSNodeDiscoveryService(localNode, nodesDatabase);
        SOSURLProtocol.getInstance().register(localStorage, nodeDiscoveryService);

        manifestsDataService = new SOSManifestsDataService(settings.getServices().getMds(), localStorage, nodeDiscoveryService);
        nodeDiscoveryService.setMDS(manifestsDataService);
    }

    /**
     * Initialise all the remaining services for this node
     *
     * The services use the localStorage to store their indices/caches to disk
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
            uk.ac.standrews.cs.sos.utils.SOS_LOG.log(LEVEL.INFO, "Node Maintainer is ENABLED");

            SettingsConfiguration.Settings.ThreadSettings threadSettings = nodeMaintainerSettings.getThread();
            nodeMaintainerService = Executors.newScheduledThreadPool(threadSettings.getPs());
            nodeMaintainerService.scheduleAtFixedRate(nodeMaintainer, threadSettings.getInitialDelay(), threadSettings.getPeriod(), NODE_MAINTAINER_TIME_UNIT);
        }
    }

    public boolean isRestEnabled() {
        return restEnabled;
    }

    public void setRestEnabled(boolean restEnabled) {
        this.restEnabled = restEnabled;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SOSLocalNode that = (SOSLocalNode) o;
        return Objects.equals(d_privateKey, that.d_privateKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), d_privateKey);
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
