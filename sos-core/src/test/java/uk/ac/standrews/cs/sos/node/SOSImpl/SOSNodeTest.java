package uk.ac.standrews.cs.sos.node.SOSImpl;

import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.node.ROLE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSNodeTest extends SetUpTest {

    protected SeaOfStuff model;

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        model = localSOSNode.getSeaOfStuff(nodeRole());
    }

    public abstract ROLE nodeRole();
}
