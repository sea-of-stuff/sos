package uk.ac.standrews.cs.sos.model.context.closures;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.model.context.ContextLoader;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderTest extends SetUpTest {

    private static final String TEST_CONTEXTS_RESOURCES_PATH = "src/test/resources/contexts/";

    @Test
    public void basicClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");

        Context context = ContextLoader.Instance(localSOSNode.getAgent(), "TestContext");
        assertEquals(context.toString(), "test worked");
    }

    @Test (expectedExceptions = ContextLoaderException.class)
    public void basicClosureLoaderFailTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "Unknown");
    }

    @Test
    public void multipleClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
    }

    @Test (expectedExceptions = ContextLoaderException.class)
    public void dirClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH);
    }

    @Test
    public void loadSomeClosureLoaderTest() throws ContextLoaderException {

        try {
            ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH);
        } catch (ContextLoaderException e) {}

        Context context = ContextLoader.Instance(localSOSNode.getAgent(),"TestContext");
        assertEquals(context.toString(), "test worked");
    }
}
