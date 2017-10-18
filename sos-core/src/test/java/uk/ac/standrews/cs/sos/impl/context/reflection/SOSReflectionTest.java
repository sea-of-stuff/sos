package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.basic.BasicMetadata;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertTrue;

/**
 * NOTE: when writing tests for contexts, make sure that you use different context name.
 * The reason is that we use the same JVM instance across all tests, so we cannot "easily" unload classes loaded in other tests
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSReflectionTest extends SetUpTest {

    private CommonUtilities commonUtilities;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        commonUtilities = new CommonUtilities(localSOSNode.getNDS(), localSOSNode.getDDS(), localSOSNode.getRMS(), localSOSNode.getStorageService());
    }

    @Test
    public void predicateConstructorLoader() throws IOException, ClassLoaderException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"true;\",\n" +
                        "\t\"dependencies\": []\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_PREDICATE);
        SOSReflection.Load(node);

        Predicate predicate = SOSReflection.PredicateInstance(node);
        assertNotNull(predicate.guid());
        assertEquals(predicate.getType(), ManifestType.PREDICATE);
        assertTrue(predicate.test(GUIDFactory.generateRandomGUID()));
    }

    @Test
    public void loadNonTrivialPredicate() throws IOException, ClassLoaderException, ManifestNotMadeException, ManifestPersistException, RoleNotFoundException, MetadataPersistException, ServiceException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\",\n" +
                        "\t\"dependencies\": []\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_PREDICATE);
        SOSReflection.Load(node);

        Predicate predicate = SOSReflection.PredicateInstance(node);
        assertNotNull(predicate.guid());
        assertEquals(predicate.getType(), ManifestType.PREDICATE);

        BasicMetadata meta = new BasicMetadata();
        meta.addProperty("Content-Type", "image/jpeg");
        meta.setGUID(GUIDFactory.generateRandomGUID());
        this.localSOSNode.getMMS().addMetadata(meta);

        Version version = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                        .setContent(GUIDFactory.generateRandomGUID())
                        .setMetadata(meta));


        assertTrue(predicate.test(version.guid()));

        // Predicate.test fails for non jpeg content
        BasicMetadata metaNonImage = new BasicMetadata();
        metaNonImage.addProperty("Content-Type", "WHATEVER");
        metaNonImage.setGUID(GUIDFactory.generateRandomGUID());
        this.localSOSNode.getMMS().addMetadata(metaNonImage);

        Version anotherVersion = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                        .setContent(GUIDFactory.generateRandomGUID())
                        .setMetadata(metaNonImage));


        assertFalse(predicate.test(anotherVersion.guid()));
    }

    @Test
    public void policyConstructorLoader() throws IOException, ClassLoaderException, PolicyException {

        String JSON_POLICY =
                "{\n" +
                        "  \"type\": \"Policy\",\n" +
                        "  \"apply\": \"\",\n" +
                        "  \"satisfied\": \"return true;\",\n" +
                        "  \"dependencies\": [],\n" +
                        "  \"fields\": [{\n" +
                        "    \"type\": \"int\",\n" +
                        "    \"name\": \"factor\",\n" +
                        "    \"value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_POLICY);
        SOSReflection.Load(node);

        Policy policy = SOSReflection.PolicyInstance(node);
        assertNotNull(policy.guid());
        assertEquals(policy.getType(), ManifestType.POLICY);
        assertTrue(policy.satisfied(null, null, null));
    }

    @Test
    public void policyFalseSatisfiedLoader() throws IOException, ClassLoaderException, PolicyException {

        String JSON_POLICY =
                "{\n" +
                        "  \"type\": \"Policy\",\n" +
                        "  \"apply\": \"\",\n" +
                        "  \"satisfied\": \"return false;\",\n" +
                        "  \"dependencies\": [],\n" +
                        "  \"fields\": [{\n" +
                        "    \"type\": \"int\",\n" +
                        "    \"name\": \"factor\",\n" +
                        "    \"value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        JsonNode node = JSONHelper.JsonObjMapper().readTree(JSON_POLICY);
        SOSReflection.Load(node);

        Policy policy = SOSReflection.PolicyInstance(node);
        assertNotNull(policy.guid());
        assertEquals(policy.getType(), ManifestType.POLICY);
        assertFalse(policy.satisfied(null, null, null));
    }


    // TODO - load policies
    // TODO - load context from JSON File, load multiple context, etc
}
