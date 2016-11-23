package uk.ac.standrews.cs.sos.actors.Client.standard;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddCompoundTest extends AgentTest {

    @Test
    public void testAddCompound() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound manifest = agent.addCompound(CompoundType.DATA, contents);
        Assert.assertEquals(manifest.getManifestType(), ManifestType.COMPOUND);

        Manifest retrievedManifest = agent.getManifest(manifest.getContentGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestType.COMPOUND);

        Collection<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    @Test
    public void testAddCompoundAndVerify() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound manifest = agent.addCompound(CompoundType.DATA, contents);
        Manifest retrievedManifest = agent.getManifest(manifest.getContentGUID());

        boolean isVerified = agent.verifyManifest(localSOSNode.getIdentity(), retrievedManifest);
        assertTrue(isVerified);
    }


}
