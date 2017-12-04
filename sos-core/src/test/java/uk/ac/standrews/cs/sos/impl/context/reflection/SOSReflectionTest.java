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
import uk.ac.standrews.cs.sos.impl.metadata.MetaProperty;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.Predicate;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertTrue;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

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

        commonUtilities = new CommonUtilities(localSOSNode.getNDS(), localSOSNode.getMDS(), localSOSNode.getUSRO(), localSOSNode.getStorageService());
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
        assertTrue(predicate.test(GUIDFactory.generateRandomGUID(GUID_ALGORITHM)));
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

        HashMap<String, MetaProperty> metadata = new HashMap<>();
        metadata.put("Content-Type", new MetaProperty("Content-Type", "image/jpeg"));
        MetadataManifest meta = new MetadataManifest(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), metadata);
        this.localSOSNode.getMMS().addMetadata(meta);

        Version version = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                        .setContent(GUIDFactory.generateRandomGUID(GUID_ALGORITHM))
                        .setMetadata(meta));

        boolean predicateResult = predicate.test(version.guid());
        assertTrue(predicateResult);

        // Predicate.test fails for non jpeg content
        HashMap<String, MetaProperty> metadataNonImage = new HashMap<>();
        metadataNonImage.put("Content-Type", new MetaProperty("Content-Type", "WHATEVER"));
        MetadataManifest metaNonImage = new MetadataManifest(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), metadataNonImage);
        this.localSOSNode.getMMS().addMetadata(metaNonImage);

        Version anotherVersion = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                        .setContent(GUIDFactory.generateRandomGUID(GUID_ALGORITHM))
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
