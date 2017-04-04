package uk.ac.standrews.cs.sos.app;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.model.Node;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.storage.interfaces.IStorage;

import java.util.List;

/**
 * The following creates a node instance of the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class ServerState {

    private static SOSLocalNode sos;

    static SOSLocalNode init(SOSConfiguration configuration) {
        try {
            return ServerState.startSOS(configuration);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    static void kill() {
        sos.kill();
    }

    private static SOSLocalNode startSOS(SOSConfiguration configuration) throws SOSException, GUIDGenerationException {

        LocalStorage localStorage;
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            IStorage storage = StorageFactory.createStorage(storageType, root);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        List<Node> bootstrapNodes = configuration.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .internalStorage(localStorage)
                .bootstrapNodes(bootstrapNodes)
                .build();

        return sos;
    }

}
