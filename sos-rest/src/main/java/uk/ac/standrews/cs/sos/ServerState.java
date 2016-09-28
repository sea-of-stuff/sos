package uk.ac.standrews.cs.sos;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;

/**
 * The following creates a node instance of the SOS.
 *
 * TODO: this class should be moved aways from this module, so that it can be shared with the webdav code and possibly other modules
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static SOSLocalNode sos;

    public static void init() {

        try {
            ServerState.startSOS();
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }
    }

    public static void init(String propertyFilePath) {
        try {
            ServerState.startSOS(propertyFilePath);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }
    }

    public static void kill() {
        sos.kill();
    }

    private static void startSOS() throws SOSException, GUIDGenerationException {
        startSOS("config.properties");
    }

    private static void startSOS(String properties) throws SOSException, GUIDGenerationException {

        File configFile = new File(properties);
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        InternalStorage internalStorage;
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage =
                    new InternalStorage(StorageFactory
                            .createStorage(storageType, root, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }



        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .internalStorage(internalStorage)
                .build();
    }

}
