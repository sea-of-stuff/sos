package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.configuration.Config;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected SOSLocalNode localSOSNode;
    protected InternalStorage internalStorage;
    protected Index index;

    @BeforeMethod
    public void setUp() throws Exception {

        Config config = Utilities.createdDummyConfig();
        try {
            internalStorage =
                    new InternalStorage(StorageFactory.createStorage(config.s_type, config.s_location, true)); // FIXME - storage have very different behaviours if mutable or not
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        try {
            index = LuceneIndex.getInstance(internalStorage);
        } catch (IndexException e) {
            throw  new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        localSOSNode = builder.config(config)
                                .index(index)
                                .internalStorage(internalStorage)
                                .build();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, InterruptedException, DataStorageException {
        index.flushDB();
        index.killInstance();

        internalStorage.destroy();
    }
}
