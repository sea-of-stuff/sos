package uk.ac.standrews.cs.sos.node;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSClient;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSCoordinator;
import uk.ac.standrews.cs.sos.node.SOSImpl.SOSStorage;

import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;

/**
 * Singleton
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSManager extends SOSNode {

    private static Configuration configuration;
    private static Index index;
    private static Identity identity;
    private static ManifestsManager manifestsManager;
    private static NodeManager nodeManager;

    private static HashMap<ROLE, SeaOfStuff> sosMap;

    private static SOSManager instance;

    private SOSManager(IGUID guid) {
        super(guid);
        // create sos instances
    }

    public static void create(Configuration configuration, IGUID guid) throws SOSException {
        SOSManager.configuration = configuration;
        instance = new SOSManager(guid);

        init();
        backgroundProcesses();

        try {
            registerSOSProtocol();
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
    }

    public static SOSManager getInstance() throws SOSException {
        if (instance == null) {
            throw new SOSException();
        }

        return instance;
    }

    public SeaOfStuff getSeaOfStuff(ROLE role) throws SOSException {
        if (instance != null && sosMap.containsKey(role)) {
            return sosMap.get(role);
        } else {
            throw new SOSException();
        }
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private static void init() throws SOSException {
        manifestsManager = new ManifestsManager(index);

        try {
            nodeManager = new NodeManager();
        } catch (NodeManagerException e) {
            throw new SOSException(e);
        }

        try {
            identity = new IdentityImpl(configuration);
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

        makeSOSInstances();

    }

    private static void makeSOSInstances() {
        sosMap = new HashMap<>();

        // TODO read configuration for roles

        sosMap.put(ROLE.CLIENT, new SOSClient(configuration, manifestsManager, identity));
        sosMap.put(ROLE.STORAGE, new SOSStorage(configuration, manifestsManager, identity));
        sosMap.put(ROLE.COORDINATOR, new SOSCoordinator(configuration, manifestsManager, identity));
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
