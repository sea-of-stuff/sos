package uk.ac.standrews.cs.sos.impl.context.closures;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.reflection.ClassLoader;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * NOTE: when writing tests for contexts, make sure that you use different context name.
 * The reason is that we use the same JVM instance across all tests, so we cannot "easily" unload classes loaded in other tests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClassLoaderTest extends SetUpTest {

    private PolicyActions policyActions;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        policyActions = new PolicyActions(localSOSNode.getNDS(), localSOSNode.getDDS(), localSOSNode.getRMS(), localSOSNode.getStorage());
    }

    @Test
    public void withGUIDContextConstructorLoader() throws IOException, ClassLoaderException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test1\",\n" +
                        "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ClassLoader.Load(node);

        IGUID guid = GUIDFactory.generateRandomGUID();
        Context context = ClassLoader.Instance("Test1", node, policyActions, guid, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertEquals(context.guid(), guid);
        assertTrue(context.getName().startsWith("Test_context"));
        assertEquals(context.getName(), "Test_context-" + guid.toMultiHash());
    }

    @Test
    public void withDomainAndCodomainContextConstructorLoader() throws IOException, ClassLoaderException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test2\",\n" +
                        "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ClassLoader.Load(node);
        Context context = ClassLoader.Instance("Test2", node, policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));
    }

    @Test
    public void contextWithPredicate() throws IOException, ClassLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test3\",\n" +
                        "    \"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ClassLoader.Load(node);
        Context context = ClassLoader.Instance("Test3", node, policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));

        SOSPredicate pred = context.predicate();
        assertNotNull(pred);


        BasicMetadata meta = new BasicMetadata();
        meta.addProperty("Content-Type", "image/jpeg");
        meta.setGUID(GUIDFactory.generateRandomGUID());
        this.localSOSNode.getMMS().addMetadata(meta);

        Version version = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                    .setContent(GUIDFactory.generateRandomGUID())
                    .setMetadata(meta));

        boolean retval = pred.test(version.guid());
        assertTrue(retval);
    }

    @Test
    public void contextWithPredicateAndPolicy() throws IOException, ClassLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test4\",\n" +
                        "    \"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\",\n" +
                        "  \t\"policies\" : [\n" +
                        "\t    \"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\"\n" +
                        "\t  ]\n" +
                        "}";


        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ClassLoader.Load(node);
        Context context = ClassLoader.Instance("Test4", node, policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));

        SOSPredicate pred = context.predicate();
        assertNotNull(pred);

        Policy[] policies = context.policies();
        assertNotNull(policies);
        assertEquals(policies.length, 1);
    }

    @Test
    public void contextWithPredicateAndMultiPolicy() throws IOException, ClassLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "\t\"name\": \"Test5\",\n" +
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\",\n" +
                        "\t\"policies\": [\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\",\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\",\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\"\n" +
                        "\t]\n" +
                        "}";


        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ClassLoader.Load(node);
        Context context = ClassLoader.Instance("Test5", node, policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));

        SOSPredicate pred = context.predicate();
        assertNotNull(pred);

        Policy[] policies = context.policies();
        assertNotNull(policies);
        assertEquals(policies.length, 3);
    }


    // TODO - load context from JSON File, load multiple context, etc
}
