package uk.ac.standrews.cs.sos.impl.context.closures;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.impl.context.ContextLoader;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderTest extends SetUpTest {

    private static final String TEST_CONTEXTS_RESOURCES_PATH = "src/test/resources/contexts/";

    @Test
    public void basicContextLoader() throws IOException, ContextLoaderException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"dependencies\": []\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test");
        assertEquals(context.toString(), "Context GUID: null, Name: null");

    }

    @Test
    public void contextWithPredicate() {
        // TODO
    }

//    @Test
//    public void basicClosureLoaderTest() throws ContextLoaderException {
//
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
//
//        Context context = ContextLoader.Instance(localSOSNode.getAgent(), "TestContext");
//        assertEquals(context.toString(), "test worked");
//    }
//
//    @Test (expectedExceptions = ContextLoaderException.class)
//    public void basicClosureLoaderFailTest() throws ContextLoaderException {
//
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "Unknown");
//    }
//
//    @Test
//    public void multipleClosureLoaderTest() throws ContextLoaderException {
//
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH, "TestContext");
//    }
//
//    @Test (expectedExceptions = ContextLoaderException.class)
//    public void dirClosureLoaderTest() throws ContextLoaderException {
//
//        ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH);
//    }
//
//    @Test
//    public void loadSomeClosureLoaderTest() throws ContextLoaderException {
//
//        try {
//            ContextLoader.Load(TEST_CONTEXTS_RESOURCES_PATH);
//        } catch (ContextLoaderException e) {}
//
//        Context context = ContextLoader.Instance(localSOSNode.getAgent(),"TestContext");
//        assertEquals(context.toString(), "test worked");
//    }
}
