package uk.ac.standrews.cs.sos.node;

import com.j256.ormlite.support.ConnectionSource;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.SOSImpl.SOSClient;
import uk.ac.standrews.cs.sos.SOSImpl.SOSCoordinator;
import uk.ac.standrews.cs.sos.SOSImpl.SOSStorage;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.db.DatabasePersistenceException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.interfaces.sos.Coordinator;
import uk.ac.standrews.cs.sos.interfaces.sos.Storage;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.sql.SQLException;

/**
 * This class represents the SOSNode of this machine.
 * This node is a singleton.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSLocalNode extends SOSNode implements LocalNode {

    private static Config config;
    private static InternalStorage internalStorage;

    private static Index index;
    private static Identity identity;
    private static ManifestsManager manifestsManager;
    private static NodeManager nodeManager;

    private static Client client;
    private static Storage storage;
    private static Coordinator coordinator;

    private static SOSLocalNode instance;
    private SOSLocalNode() throws GUIDGenerationException {

        super(GUIDFactory.recreateGUID(config.n_id),
                config.n_hostname,
                config.n_port,
                config.n_is_client,
                config.n_is_storage,
                config.n_is_coordinator);
    }

    /**
     * Create the LocalSOSNode.
     * The index must be already set using setIndex();
     *
     * @throws SOSException
     */
    public static void create() throws SOSException, SOSProtocolException {
        config = hardcodedConfiguration();
        try {
            internalStorage =
                    new InternalStorage(StorageFactory.createStorage(config.s_type, config.s_location, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException  | DataStorageException e) {
            throw new SOSException(e);
        }


        try {
            index = LuceneIndex.getInstance(internalStorage);
        } catch (IndexException e) {
            throw  new SOSException(e);
        }

        // TODO : readSystemProperties();

        try {
            instance = new SOSLocalNode();
        } catch (GUIDGenerationException e) {
            throw new SOSException(e);
        }

        initManifestManager();
        initNodeManager();
        initIdentity();
        initSOSInstances();

        backgroundProcesses();
        registerSOSProtocol();
    }

    // REMOVEME - use system properties or info passed by user
    private static Config hardcodedConfiguration() {
        Config retval = null;

        Config.db_type = Config.DB_TYPE_SQLITE;
        try {
            Config.initDatabaseInfo();
        } catch (PersistenceException | IOException e) {
            e.printStackTrace();
        }

        try {
            ConnectionSource connection = SQLDB.getSQLConnection();
            retval = SQLDB.getConfiguration(connection);
        } catch (DatabasePersistenceException | SQLException e) {
            e.printStackTrace();
        }

        return retval;
    }

    /**
     * Get the instance of this node.
     * @return
     * @throws SOSException if the manager was not created using the create() method
     */
    public static SOSLocalNode getInstance() throws SOSException {
        if (instance == null) {
            throw new SOSException();
        }

        return instance;
    }

    public Client getClient() {
        return client;
    }

    public Storage getStorage() {
        return storage;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public InternalStorage getInternalStorage() {
        return internalStorage;
    }

    public Index getIndex() {
        return index;
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private static void initManifestManager() {
        manifestsManager = new ManifestsManager(internalStorage, index);
    }

    private static void initNodeManager() throws SOSException {
        try {
            nodeManager = new NodeManager();
        } catch (NodeManagerException e) {
            throw new SOSException(e);
        }
    }


    private static void initIdentity() throws SOSException {
        try {
            identity = new IdentityImpl();
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

    }

    private static void initSOSInstances() {
        // TODO - create instances based on configs

        client = new SOSClient(internalStorage, manifestsManager, identity);
        storage = new SOSStorage(internalStorage, manifestsManager, identity);
        coordinator = new SOSCoordinator(manifestsManager, identity, nodeManager);
    }

    private static void registerSOSProtocol() throws SOSProtocolException {
        try {
            if (!SOSURLStreamHandlerFactory.URLStreamHandlerFactoryIsSet) {
                URLStreamHandlerFactory urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(nodeManager);
                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            }
        } catch (Error e) {
            throw new SOSProtocolException(e);
        }
    }

    private static void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

}
