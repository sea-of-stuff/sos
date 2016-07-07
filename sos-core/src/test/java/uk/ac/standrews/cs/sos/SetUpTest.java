package uk.ac.standrews.cs.sos;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.exceptions.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest {

    protected SOSLocalNode localSOSNode;
    protected InternalStorage internalStorage;

    @BeforeMethod
    public void setUp() throws Exception {
        HelperTest.CreateDBTestDump();

        SOSLocalNode.create();
        localSOSNode = SOSLocalNode.getInstance();
        internalStorage = localSOSNode.getInternalStorage();
    }

    @AfterMethod
    public void tearDown() throws IOException, IndexException, InterruptedException, DataStorageException {
        localSOSNode.getIndex().flushDB();
        localSOSNode.getIndex().killInstance();

        internalStorage.destroy();

        //Thread.sleep(1000);
    }
}
