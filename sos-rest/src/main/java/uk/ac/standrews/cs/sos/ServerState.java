package uk.ac.standrews.cs.sos;

import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static SOSLocalNode sos;

    private static InternalStorage internalStorage;
    private static Index index;

    public static void init() {
        System.out.println("Starting SOS");
        ServerState.startSOS();
        System.out.println("SOS started");
    }

    public static void kill() {
        // TODO
    }

    private static void startSOS() {
        try {
            Config config = hardcodedConfiguration();

            internalStorage =
                    new InternalStorage(StorageFactory.createStorage(config.s_type, config.s_location, true)); // FIXME - storage have very different behaviours if mutable or not

            index = LuceneIndex.getInstance(internalStorage);

            SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
            ServerState.sos = builder.config(config)
                    .index(index)
                    .internalStorage(internalStorage)
                    .build();

        } catch (IndexException | StorageException | DataStorageException
                 | IOException | PersistenceException
                | SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }
    }

    private static Config hardcodedConfiguration() throws IOException, PersistenceException {
        Config.db_type = Config.DB_TYPE_SQLITE;
        Config.initDatabaseInfo();

        return new Config();
    }
}
