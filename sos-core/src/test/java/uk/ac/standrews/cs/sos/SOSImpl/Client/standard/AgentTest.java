package uk.ac.standrews.cs.sos.SOSImpl.Client.standard;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.sos.Agent;

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

    @Test(expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifest() throws Exception {
        agent.getManifest(GUIDFactory.generateRandomGUID());
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void testFailGetManifestNull() throws Exception {
        agent.getManifest(null);
    }

}
