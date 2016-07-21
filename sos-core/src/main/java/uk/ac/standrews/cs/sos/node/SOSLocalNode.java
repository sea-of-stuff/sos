package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SOSImpl.SOSClient;
import uk.ac.standrews.cs.sos.SOSImpl.SOSCoordinator;
import uk.ac.standrews.cs.sos.SOSImpl.SOSStorage;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.node.NodeDatabase;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.interfaces.sos.Coordinator;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManagerImpl;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.database.DatabaseType;
import uk.ac.standrews.cs.sos.node.database.SQLDatabase;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * This class represents the SOSNode of this machine.
 * Using a BuilderPattern to construct this node.
 *
 * A SOSLocalNode may expose multiple SOS interfaces to the caller: Client, Storage and Coordinator.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocalNode extends SOSNode implements LocalNode {

    private Config config;
    private InternalStorage internalStorage;
    private Index index;
    private PolicyManager policyManager;

    private NodeDatabase nodeDatabase;

    private Identity identity;
    private ManifestsManager manifestsManager;
    private NodeManager nodeManager;

    private Client client;
    private Storage storage;
    private Coordinator coordinator;

    public SOSLocalNode(Builder builder) throws SOSException, GUIDGenerationException {
        super(GUIDFactory.recreateGUID(builder.config.n_id),
                builder.config.n_hostname,
                builder.config.n_port,
                builder.config.n_is_client,
                builder.config.n_is_storage,
                builder.config.n_is_coordinator);

        config = builder.config;
        internalStorage = builder.internalStorage;
        index = builder.index;
        policyManager = builder.policyManager; //FIXME - could have different policies for client, storage, coordinator!

        try {
            nodeDatabase = new SQLDatabase(new DatabaseType(Config.db_type),
                                Config.DB_DUMP_FILE.getPathname());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        }

        initManifestManager();
        initNodeManager();
        initIdentity();
        initSOSInstances();

        backgroundProcesses();

        try {
            registerSOSProtocol();
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
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
    public Coordinator getCoordinator() {
        return coordinator;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private void initManifestManager() {
        manifestsManager = new ManifestsManagerImpl(internalStorage, index, nodeManager);
    }

    private void initNodeManager() throws SOSException {
        try {
            nodeManager = new NodeManager(this, nodeDatabase);
        } catch (NodeManagerException e) {
            throw new SOSException(e);
        }
    }

    private void initIdentity() throws SOSException {
        try {
            identity = new IdentityImpl();
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

    }

    private void initSOSInstances() {
        // TODO - create instances based on configs

        client = new SOSClient(this, internalStorage, manifestsManager, identity);
        storage = new SOSStorage(this, internalStorage, manifestsManager, identity);
        coordinator = new SOSCoordinator(manifestsManager, identity, nodeManager);
    }

    private void registerSOSProtocol() throws SOSProtocolException {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory =
                        new SOSURLStreamHandlerFactory(internalStorage, nodeManager);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            throw new SOSProtocolException(e);
        }
    }

    private void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    /**
     * This is the builder for the SOSLocalNode.
     *
     *
     * TODO - defaults
     */
    public static class Builder {
        private static Config config;
        private static InternalStorage internalStorage;
        private static Index index;
        private static PolicyManager policyManager;

        public Builder config(Config config) {
            this.config = config;
            return this;
        }

        public Builder internalStorage(InternalStorage internalStorage) {
            this.internalStorage = internalStorage;
            return this;
        }

        public Builder index(Index index) {
            this.index = index;
            return this;
        }

        public Builder policies(PolicyManager policyManager) {
            this.policyManager = policyManager;
            return this;
        }

        public SOSLocalNode build() throws SOSException, GUIDGenerationException {
            return new SOSLocalNode(this);
        }
    }

}
