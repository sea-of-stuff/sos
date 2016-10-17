package uk.ac.standrews.cs.sos.app;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.storage.InternalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.util.List;

/**
 * The following creates a node instance of the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static SOSLocalNode sos;

    public static SOSLocalNode init(String propertyFilePath) {
        try {
            return ServerState.startSOS(propertyFilePath);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void kill() {
        sos.kill();
    }

    private static SOSLocalNode startSOS(String properties) throws SOSException, GUIDGenerationException {

        File configFile = new File(properties);
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        InternalStorage internalStorage;
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage = new InternalStorage(StorageFactory
                            .createStorage(storageType, root, false)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        PolicyManager policyManager = configuration.getPolicyManager();
        List<Node> bootstrapNodes = configuration.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .internalStorage(internalStorage)
                .policies(policyManager)
                .bootstrapNodes(bootstrapNodes)
                .build();

        return sos;
    }

}
