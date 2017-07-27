package uk.ac.standrews.cs.sos.impl.context.closures;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.context.ContextLoaderException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.impl.context.PolicyActions;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextLoader;
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
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextLoaderTest extends SetUpTest {

    private PolicyActions policyActions;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        policyActions = new PolicyActions(localSOSNode.getNDS(), localSOSNode.getDDS(), localSOSNode.getRMS(), localSOSNode.getStorage());
    }

    @Test
    public void withGUIDContextConstructorLoader() throws IOException, ContextLoaderException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        IGUID guid = GUIDFactory.generateRandomGUID();
        Context context = ContextLoader.Instance("Test", policyActions, guid, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertEquals(context.guid(), guid);
        assertTrue(context.getName().startsWith("Test_context"));
        assertEquals(context.getName(), "Test_context-" + guid);
    }

    @Test
    public void withDomainAndCodomainContextConstructorLoader() throws IOException, ContextLoaderException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"predicate\": \"CommonPredicates.AcceptAll();\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));
    }

    @Test
    public void contextWithPredicate() throws IOException, ContextLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\"\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));

        SOSPredicate pred = context.predicate();
        assertNotNull(pred);


        BasicMetadata meta = new BasicMetadata();
        meta.addProperty("Content-Type", "image/jpeg");
        meta.setGUID(GUIDFactory.generateRandomGUID());
        this.localSOSNode.getMMS().addMetadata(meta);

        Version version = this.localSOSNode.getAgent().addVersion(new VersionBuilder()
                .setContent(GUIDFactory.generateRandomGUID())
                .setMetadata(meta));

        boolean retval = pred.test(version.guid());
        assertTrue(retval);
    }

    @Test
    public void contextWithPredicateAndPolicy() throws IOException, ContextLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "    \"name\": \"Test\",\n" +
                        "    \"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\",\n" +
                        "  \t\"policies\" : [\n" +
                        "\t    \"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\"\n" +
                        "\t  ]\n" +
                        "}";


        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

        assertNotNull(context.guid());
        assertTrue(context.getName().startsWith("Test_context"));

        SOSPredicate pred = context.predicate();
        assertNotNull(pred);

        Policy[] policies = context.policies();
        assertNotNull(policies);
        assertEquals(policies.length, 1);
    }

    @Test
    public void contextWithPredicateAndMultiPolicy() throws IOException, ContextLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, NodesCollectionException {

        String JSON_CONTEXT =
                "{\n" +
                        "\t\"name\": \"Test\",\n" +
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\",\n" +
                        "\t\"policies\": [\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\",\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\",\n" +
                        "\t\t\"CommonPolicies.ManifestReplicationPolicy(policyActions, codomain, 1)\"\n" +
                        "\t]\n" +
                        "}";


        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_CONTEXT);

        ContextLoader.LoadContext(node);

        Context context = ContextLoader.Instance("Test", policyActions, "Test_context", new NodesCollectionImpl(NodesCollection.TYPE.LOCAL), new NodesCollectionImpl(NodesCollection.TYPE.LOCAL));

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
