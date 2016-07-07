package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected LocalSOSNode localSOSNode;
    protected InternalStorage internalStorage;
    protected Index index;

    @BeforeMethod
    public void setUp() throws Exception {
        HelperTest.CreateDBTestDump();

        index = LuceneIndex.getInstance();

        LocalSOSNode.setIndex(index);
        LocalSOSNode.create();
        localSOSNode = LocalSOSNode.getInstance();
        internalStorage = localSOSNode.getInternalStorage();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, InterruptedException, DataStorageException {
        index.flushDB();
        index.killInstance();

        internalStorage.destroy();


//        HelperTest.DeletePath(configuration.getIndexDirectory());
//        HelperTest.DeletePath(configuration.getManifestsDirectory());
//        HelperTest.DeletePath(configuration.getTestDataDirectory());
//        HelperTest.DeletePath(configuration.getDataDirectory());
//
//        HelperTest.DeletePath(Config.DB_DIRECTORY);

        //Thread.sleep(1000);
    }
}
