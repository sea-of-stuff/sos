package uk.ac.standrews.cs.sos.model.context.closures;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.model.context.ContextLoader;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderTest {

    private static final String TEST_CLOSURES_RESOURCES_PATH = "src/test/resources/contexts/";

    @Test
    public void basicClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");

        Context context = ContextLoader.Instance("TestClosure");
        assertEquals(context.toString(), "test worked");
    }

    @Test (expectedExceptions = ContextLoaderException.class)
    public void basicClosureLoaderFailTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "Unknown");
    }

    @Test
    public void multipleClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
    }

    @Test (expectedExceptions = ContextLoaderException.class)
    public void dirClosureLoaderTest() throws ContextLoaderException {

        ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH);
    }

    @Test
    public void loadSomeClosureLoaderTest() throws ContextLoaderException {

        try {
            ContextLoader.Load(TEST_CLOSURES_RESOURCES_PATH);
        } catch (ContextLoaderException e) {}

        Context context = ContextLoader.Instance("TestClosure");
        assertEquals(context.toString(), "test worked");
    }
}
