package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;
import uk.ac.standrews.cs.sos.utils.HelperTest;
import uk.ac.standrews.cs.storage.exceptions.DestroyException;
import uk.ac.standrews.cs.storage.interfaces.IStorage;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected LocalSOSNode localSOSNode;
    protected IStorage internalStorage;
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
    public void tearDown() throws IOException, IndexException, DestroyException, InterruptedException {
        index.flushDB();
        index.killInstance();

//        HelperTest.DeletePath(configuration.getIndexDirectory());
//        HelperTest.DeletePath(configuration.getManifestsDirectory());
//        HelperTest.DeletePath(configuration.getTestDataDirectory());
//        HelperTest.DeletePath(configuration.getDataDirectory());
//
//        HelperTest.DeletePath(Config.DB_DIRECTORY);

        //internalStorage.destroy();
        //Thread.sleep(1000);
    }
}
