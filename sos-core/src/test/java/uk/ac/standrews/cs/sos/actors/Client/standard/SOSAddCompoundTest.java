package uk.ac.standrews.cs.sos.actors.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.ContentImpl;

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

        Compound manifest = agent.addCompound(CompoundType.DATA, contents);
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

        Compound manifest = agent.addCompound(CompoundType.DATA, contents);
        Manifest retrievedManifest = agent.getManifest(manifest.guid());

        boolean isVerified = agent.verifyManifest(localSOSNode.getIdentity(), retrievedManifest);
        assertTrue(isVerified);
    }


}
