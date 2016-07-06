package uk.ac.standrews.cs.sos.node.SOSImpl.Storage;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.interfaces.node.Storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StorageTest extends SetUpTest {

    protected Storage storage;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        storage = localSOSNode.getStorage();
    }

}
