package uk.ac.standrews.cs.sos.impl.services.Context;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddRetrieveContextTest extends ContextServiceTest {

    private static final String FAT_CONTEXT_1 = "{\n" +
            "\t\"context\": {\n" +
            "\t\t\"name\": \"All\",\n" +
            "\t\t\"domain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"codomain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"max_age\": 0\n" +
            "\t},\n" +
            "\t\"predicate\": {\n" +
            "\t\t\"type\": \"Predicate\",\n" +
            "\t\t\"predicate\": \"true;\",\n" +
            "\t\t\"dependencies\": []\n" +
            "\t},\n" +
            "\t\"policies\": []\n" +
            "}";

    // Max Age set to 10 seconds
    private static final String FAT_CONTEXT_2 = "{\n" +
            "\t\"context\": {\n" +
            "\t\t\"name\": \"All\",\n" +
            "\t\t\"domain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"codomain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"max_age\": 10\n" +
            "\t},\n" +
            "\t\"predicate\": {\n" +
            "\t\t\"type\": \"Predicate\",\n" +
            "\t\t\"predicate\": \"true;\",\n" +
            "\t\t\"dependencies\": []\n" +
            "\t},\n" +
            "\t\"policies\": []\n" +
            "}";

    private static final String FAT_CONTEXT_3 = "{\n" +
            "  \"context\": {\n" +
            "    \"name\": \"data_replication_1\",\n" +
            "    \"domain\": {\n" +
            "      \"type\": \"LOCAL\",\n" +
            "      \"nodes\": []\n" +
            "    },\n" +
            "    \"codomain\": {\n" +
            "      \"type\": \"SPECIFIED\",\n" +
            "      \"nodes\": [\"SHA256_16_924d9fa80b1e409741686775a197b2ae48ef4b5d6c4189af888b0111b6bb47f2\"]\n" +
            "    },\n" +
            "    \"max_age\": 0\n" +
            "  },\n" +
            "  \"predicate\": {\n" +
            "    \"type\": \"Predicate\",\n" +
            "    \"predicate\": \"CommonPredicates.AcceptAll();\",\n" +
            "    \"dependencies\": []\n" +
            "  },\n" +
            "  \"policies\": [\n" +
            "    {\n" +
            "      \"type\" : \"Policy\",\n" +
            "      \"apply\" : \"CommonPolicies.replicateData(codomain, utilities, manifest, factor);\",\n" +
            "      \"satisfied\" : \"return CommonPolicies.dataIsReplicated(codomain, utilities, manifest, factor);\",\n" +
            "      \"dependencies\" : [],\n" +
            "      \"fields\" : [\n" +
            "        {\n" +
            "          \"type\" : \"int\",\n" +
            "          \"name\" : \"factor\",\n" +
            "          \"value\" : \"1\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";


    private static final String FAT_CONTEXT_4 = "{\n" +
            "  \"context\": {\n" +
            "    \"name\": \"data_replication_2\",\n" +
            "    \"domain\": {\n" +
            "      \"type\": \"LOCAL\",\n" +
            "      \"nodes\": []\n" +
            "    },\n" +
            "    \"codomain\": {\n" +
            "      \"type\": \"SPECIFIED\",\n" +
            "      \"nodes\": [\"SHA256_16_924d9fa80b1e409741686775a197b2ae48ef4b5d6c4189af888b0111b6bb47f2\", \"SHA256_16_c2134ef5253f507dcda39b25e9a999769c1bd5e337145de1662a118682a76cc0\"]\n" +
            "    },\n" +
            "    \"max_age\": 0\n" +
            "  },\n" +
            "  \"predicate\": {\n" +
            "    \"type\": \"Predicate\",\n" +
            "    \"predicate\": \"CommonPredicates.AcceptAll();\",\n" +
            "    \"dependencies\": []\n" +
            "  },\n" +
            "  \"policies\": [\n" +
            "    {\n" +
            "      \"type\" : \"Policy\",\n" +
            "      \"apply\" : \"CommonPolicies.replicateData(codomain, utilities, manifest, factor);\",\n" +
            "      \"satisfied\" : \"return CommonPolicies.dataIsReplicated(codomain, utilities, manifest, factor);\",\n" +
            "      \"dependencies\" : [],\n" +
            "      \"fields\" : [\n" +
            "        {\n" +
            "          \"type\" : \"int\",\n" +
            "          \"name\" : \"factor\",\n" +
            "          \"value\" : \"1\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    @Test
    public void addContextTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        assertNotNull(guid);
        assertFalse(guid.isInvalid());
    }

    @Test
    public void addContextAndGetItBackTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Context context = contextService.getContext(guid);
        assertNotNull(context);
        assertEquals(context.guid(), guid);
        assertEquals(context.getName(), "All");
        assertNotEquals(context.size(), -1);
    }

    @Test
    public void addContextAndGetNoContentsTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Set<IGUID> contents = contextService.getContents(guid);
        assertNotNull(contents);
        assertEquals(contents.size(), 0);
    }

    @Test
    public void addContextRunPredicateAndGetNoContentsTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        int runs = contextService.runPredicates();
        assertEquals(runs, 0);
        Set<IGUID> contents = contextService.getContents(guid);
        assertNotNull(contents);
        assertEquals(contents.size(), 0);
    }

    @Test
    public void addContext_RunPredicate_GetOneContent_Test() throws ContextException, ServiceException, TIPNotFoundException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Context context = contextService.getContext(guid);

        // START - ADD ATOM AND VERSION
        AtomBuilder atomBuilder = new AtomBuilder().setData(new StringData("TEST"));
        Atom atom = agent.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Version manifest = agent.addVersion(builder);
        // END - ADD ATOM AND VERSION

        int runs = contextService.runPredicates();
        assertEquals(runs, 1);
        Context contextTip = contextService.getContextTIP(context.invariant());
        Set<IGUID> contents = contextService.getContents(contextTip.guid());
        assertNotNull(contents);
        assertEquals(contents.size(), 1);
    }

    @Test
    public void addContextRunPredicateMultipleTimesAndGetMultipleContentsCombiningContextVersionsTest() throws ContextException, ServiceException, TIPNotFoundException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_2);
        Context context = contextService.getContext(guid);

        // START - ADD ATOM AND VERSION
        AtomBuilder atomBuilder = new AtomBuilder().setData(new StringData("TEST"));
        Atom atom = agent.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Version manifest = agent.addVersion(builder);
        // END - ADD ATOM AND VERSION

        int runs = contextService.runPredicates();
        assertEquals(runs, 1);

        // START_2 - ADD ATOM AND VERSION
        atomBuilder = new AtomBuilder().setData(new StringData("TEST-TEST"));
        atom = agent.addAtom(atomBuilder);

        builder = new VersionBuilder(atom.guid());
        manifest = agent.addVersion(builder);
        // END_2 - ADD ATOM AND VERSION

        runs = contextService.runPredicates();
        assertEquals(runs, 2); // Runs twice, but uses cached result for "TEST"

        Context contextTip = contextService.getContextTIP(context.invariant());
        assertNotNull(contextTip.previous());
        assertFalse(contextTip.previous().isEmpty());
        Set<IGUID> contents = contextService.getContents(contextTip.guid());
        assertNotNull(contents);
        assertEquals(contents.size(), 2);
    }

    @Test
    public void sameContextsButDifferentDomains() throws ContextException {

        IGUID guid_3 = contextService.addContext(FAT_CONTEXT_3);
        assertNotNull(guid_3);
        assertFalse(guid_3.isInvalid());
        Context context_3 = contextService.getContext(guid_3);
        assertNotNull(context_3);

        IGUID guid_4 = contextService.addContext(FAT_CONTEXT_4);
        assertNotNull(guid_4);
        assertFalse(guid_4.isInvalid());
        Context context_4 = contextService.getContext(guid_4);
        assertNotNull(context_4);

        Set<Context> contexts = contextService.getContexts();
        assertEquals(contexts.size(), 2);
    }

    // TODO - test max age property and getting contents from multiple contexts (w previous) WITH EXPIRATION
}
