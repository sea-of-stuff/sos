package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.util.List;

/**
 * The following creates a node instance of the SOS.
 * THIS CLASS SHOULD BE USED FOR TESTING PURPOSES ONLY
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public SOSLocalNode sos;
    private LocalStorage localStorage;

    public SOSLocalNode init(String propertyFilePath) {
        try {
            return startSOS(propertyFilePath);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void kill() throws DataStorageException {
        sos.kill();

        localStorage.destroy();
    }

    private SOSLocalNode startSOS(String properties) throws SOSException, GUIDGenerationException {

        File configFile = new File(properties);
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            localStorage =
                    new LocalStorage(StorageFactory
                            .createStorage(storageType, root));
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        PolicyManager policyManager = configuration.getPolicyManager();
        List<Node> bootstrapNodes = configuration.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .internalStorage(localStorage)
                .policies(policyManager)
                .bootstrapNodes(bootstrapNodes)
                .build();

        return sos;
    }

}
