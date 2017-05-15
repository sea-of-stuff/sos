package uk.ac.standrews.cs.sos.impl.actors.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.impl.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.model.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddCompoundTest extends AgentTest {

    @Test
    public void testAddCompound() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound manifest = agent.addCompound(compoundBuilder);
        Assert.assertEquals(manifest.getType(), ManifestType.COMPOUND);

        Manifest retrievedManifest = agent.getManifest(manifest.guid());
        assertEquals(retrievedManifest.getType(), ManifestType.COMPOUND);

        Set<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    @Test
    public void testAddCompoundAndVerify() throws Exception {
        Content cat = new ContentImpl("cat", GUIDFactory.recreateGUID("123"));
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(cat);

        CompoundBuilder compoundBuilder = new CompoundBuilder()
                .setType(CompoundType.DATA)
                .setContents(contents);
        Compound manifest = agent.addCompound(compoundBuilder);
        Manifest retrievedManifest = agent.getManifest(manifest.guid());

        boolean isVerified = agent.verifyManifest(localSOSNode.getRMS().active(), retrievedManifest);
        assertTrue(isVerified);
    }

}