package uk.ac.standrews.cs.sos.impl.context.closures;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.SOSPredicate;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderTest extends SetUpTest {

    private static final String TEST_CONTEXTS_RESOURCES_PATH = "src/test/resources/contexts/";

    private PolicyActions policyActions;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        policyActions = new PolicyActions(localSOSNode.getNDS(), localSOSNode.getDDS(), localSOSNode.getRMS(), localSOSNode.getStorage());
    }

    @Test
    public void withGUIDContextConstructorLoader() throws IOException, ContextLoaderException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        IGUID guid = GUIDFactory.generateRandomGUID();
        Context context = ContextLoader.Instance("Test", policyActions, guid, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertEquals(context.guid(), guid);
        assertEquals(context.getName(), "Test_context");

    }

    @Test
    public void withDomainAndCodomainContextConstructorLoader() throws IOException, ContextLoaderException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertEquals(context.getName(), "Test_context");
    }

    @Test
    public void contextWithPredicate() throws IOException, ContextLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"predicate\": \"CommonPredicates.ContentTypePredicate(Collections.singletonList(\\\"image/jpeg\\\"));\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertEquals(context.getName(), "Test_context");

        SOSPredicate pred = context.predicate();
        assertNotNull(context.predicate());


        BasicMetadata meta = new BasicMetadata();
        meta.addProperty("content-type", "image/jpeg");
        meta.setGUID(GUIDFactory.generateRandomGUID());

        Version version = this.localSOSNode.getAgent().addVersion(new VersionBuilder()
                .setContent(GUIDFactory.generateRandomGUID())
                .setMetadata(meta));

        boolean retval = pred.test(version.guid());
        System.out.println(retval);

        // FIXME - the CommonPredicate is never run correctly
    }


    // TODO - load context from JSON File, load multiple context, etc
}
