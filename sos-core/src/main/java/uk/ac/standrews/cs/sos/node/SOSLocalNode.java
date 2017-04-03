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
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.node.Database;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.model.metadata.tika.TikaMetadataEngine;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.node.directory.DatabaseImpl;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
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
    private Database database;
    private Identity identity;

    private ScheduledExecutorService cacheFlusherService;

    // Node roles
    // All roles will share storage, node directory, manifests directory, etc.
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

    public SOSLocalNode() throws SOSException, GUIDGenerationException {
        super(Builder.configuration);

        SOS_LOG.log(LEVEL.INFO, "Starting up node ");

        SOSConfiguration configuration = Builder.configuration;
        localStorage = Builder.localStorage;

        try {
            String dbFilename = configuration.getDBFilename();
            File file = localStorage.createFile(localStorage.getDBDirectory(), dbFilename).toFile();

            database = new DatabaseImpl(file.getPath());
        } catch (DatabaseException e) {
            throw new SOSException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initNDS();
        loadBootstrapNodes(Builder.bootstrapNodes);
        registerNode(configuration.getNodePort());

        initIdentity();

        initSOSInstances();
        cacheFlusher();

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
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public void kill() {
        dds.flush();
        storage.flush();

        cacheFlusherService.shutdown();

        RequestsManager.getInstance().shutdown();
    }

    private void loadBootstrapNodes(List<Node> bootstrapNodes)
            throws NodeRegistrationException {

        for(Node node:bootstrapNodes) {
            nds.registerNode(node, true);
        }
    }

    private void registerNode(int port) {

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.hostAddress = new InetSocketAddress(inetAddress, port);

            nds.registerNode(this, false);
        } catch (UnknownHostException | NodeRegistrationException e) {
            SOS_LOG.log(LEVEL.ERROR, e.getMessage());
        }

    }

    private void initIdentity() throws SOSException {
        try {
            identity = new IdentityImpl();
        } catch (KeyGenerationException | KeyLoadedException e) {
            throw new SOSException(e);
        }

    }

    private void initNDS() throws SOSException {
        try {
            SOSURLProtocol.getInstance().register(localStorage);
            nds = new SOSNDS(this, database);
            SOSURLProtocol.getInstance().setNDS(nds);
        } catch (SOSProtocolException e) {
            throw new SOSException(e);
        }
    }

    private void initSOSInstances() {
        MetadataEngine metadataEngine = new TikaMetadataEngine();

        dds = new SOSDDS(localStorage, nds);
        storage = new SOSStorage(this, localStorage, nds, dds);
        mms = new SOSMMS(dds, metadataEngine);
        cms = new SOSCMS(dds);
        rms = new SOSRMS();

        agent = SOSAgent.instance(storage, dds, mms, cms, identity);
    }

    private void cacheFlusher() {
        SOS_LOG.log(LEVEL.INFO, "Cache Flusher started");

        CacheFlusher cacheFlusher = new CacheFlusher(localStorage);

        cacheFlusherService = Executors.newScheduledThreadPool(1);
        cacheFlusherService.scheduleAtFixedRate(cacheFlusher, 0, CacheFlusher.PERIOD, CacheFlusher.TIME_UNIT);
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
