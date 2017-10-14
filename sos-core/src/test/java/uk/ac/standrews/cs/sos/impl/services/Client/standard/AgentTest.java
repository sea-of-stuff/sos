package uk.ac.standrews.cs.sos.impl.services.Client.standard;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.services.Agent;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AgentTest extends SetUpTest {

    protected Agent agent;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        agent = localSOSNode.getAgent();
    }

    @Override
    @AfterMethod
    public void tearDown() throws InterruptedException, DataStorageException, IOException {
        super.tearDown();
    }

    @Test(expectedExceptions = ServiceException.class)
    public void testFailGetManifest() throws Exception {
        agent.getManifest(GUIDFactory.generateRandomGUID());
    }

    @Test (expectedExceptions = ServiceException.class)
    public void testFailGetManifestNull() throws Exception {
        agent.getManifest(null);
    }

}
