package uk.ac.standrews.cs.sos;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
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
        System.out.println("Starting SOS");

        try {
            ServerState.startSOS();
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        System.out.println("SOS started");
    }

    public static void kill() {
        sos.kill();
    }

    private static void startSOS() throws SOSException, GUIDGenerationException {

        File configFile = new File("config.properties"); // TODO - properties should be passed as a param
        SOSConfiguration configuration = new SOSConfiguration(configFile);

        InternalStorage internalStorage;
        Index index;
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage =
                    new InternalStorage(StorageFactory
                            .createStorage(storageType, root, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        try {
            index = LuceneIndex.getInstance(internalStorage);
        } catch (IndexException e) {
            throw  new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .index(index)
                .internalStorage(internalStorage)
                .build();
    }

}
