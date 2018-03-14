/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.context.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.PolicyException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.exceptions.reflection.ClassLoaderException;
import uk.ac.standrews.cs.sos.impl.context.CommonUtilities;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataManifest;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
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
                        "\t\"predicate\": \"true;\"\n" +
                        "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);
        SOSReflection.instance().load(node);

        Predicate predicate = SOSReflection.instance().predicateInstance(node);
        assertNotNull(predicate.guid());
        assertEquals(predicate.getType(), ManifestType.PREDICATE);
        assertTrue(predicate.test(GUIDFactory.generateRandomGUID(GUID_ALGORITHM)));
    }

    @Test
    public void loadNonTrivialPredicate() throws IOException, ClassLoaderException, ManifestNotMadeException, MetadataPersistException, ServiceException {

        String JSON_PREDICATE =
                "{\n" +
                        "\t\"type\": \"Predicate\",\n" +
                        "\t\"predicate\": \"CommonPredicates.ContentTypePredicate(guid, Collections.singletonList(\\\"image/jpeg\\\"));\"\n" +
                        "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(JSON_PREDICATE);
        SOSReflection.instance().load(node);

        Predicate predicate = SOSReflection.instance().predicateInstance(node);
        assertNotNull(predicate.guid());
        assertEquals(predicate.getType(), ManifestType.PREDICATE);

        HashMap<String, Property> metadata = new HashMap<>();
        metadata.put("Content-Type", new Property("Content-Type", "image/jpeg"));
        MetadataManifest meta = new MetadataManifest(metadata, null);
        this.localSOSNode.getMMS().addMetadata(meta);

        Version version = this.localSOSNode.getAgent()
                .addVersion(new VersionBuilder()
                        .setContent(GUIDFactory.generateRandomGUID(GUID_ALGORITHM))
                        .setMetadata(meta));

        boolean predicateResult = predicate.test(version.guid());
        assertTrue(predicateResult);

        // Predicate.test fails for non jpeg content
        HashMap<String, Property> metadataNonImage = new HashMap<>();
        metadataNonImage.put("Content-Type", new Property("Content-Type", "WHATEVER"));
        MetadataManifest metaNonImage = new MetadataManifest(metadataNonImage, null);
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
                        "  \"fields\": [{\n" +
                        "    \"type\": \"int\",\n" +
                        "    \"name\": \"factor\",\n" +
                        "    \"value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(JSON_POLICY);
        SOSReflection.instance().load(node);

        Policy policy = SOSReflection.instance().policyInstance(node);
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
                        "  \"fields\": [{\n" +
                        "    \"type\": \"int\",\n" +
                        "    \"name\": \"factor\",\n" +
                        "    \"value\": \"2\"\n" +
                        "  }]\n" +
                        "}";

        JsonNode node = JSONHelper.jsonObjMapper().readTree(JSON_POLICY);
        SOSReflection.instance().load(node);

        Policy policy = SOSReflection.instance().policyInstance(node);
        assertNotNull(policy.guid());
        assertEquals(policy.getType(), ManifestType.POLICY);
        assertFalse(policy.satisfied(null, null, null));
    }

}
