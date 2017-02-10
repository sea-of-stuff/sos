package uk.ac.standrews.cs.sos.model.context.closures;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.context.ClosureLoaderException;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureLoaderTest {

    private static final String TEST_CLOSURES_RESOURCES_PATH = "src/test/resources/closures/";

    @Test
    public void basicClosureLoaderTest() throws ClosureLoaderException {

        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");

        Closure closure = ClosureLoader.Instance("TestClosure");
        assertEquals(closure.toString(), "test worked");
    }

    @Test (expectedExceptions = ClosureLoaderException.class)
    public void basicClosureLoaderFailTest() throws ClosureLoaderException {

        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "Unknown");
    }

    @Test
    public void multipleClosureLoaderTest() throws ClosureLoaderException {

        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH, "TestClosure");
    }

    @Test (expectedExceptions = ClosureLoaderException.class)
    public void dirClosureLoaderTest() throws ClosureLoaderException {

        ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH);
    }

    @Test
    public void loadSomeClosureLoaderTest() throws ClosureLoaderException {

        try {
            ClosureLoader.Load(TEST_CLOSURES_RESOURCES_PATH);
        } catch (ClosureLoaderException e) {}

        Closure closure = ClosureLoader.Instance("TestClosure");
        assertEquals(closure.toString(), "test worked");
    }
}
