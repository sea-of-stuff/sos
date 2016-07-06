package uk.ac.standrews.cs.sos.node.SOSImpl.Client;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.Assert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddCompoundTest extends ClientTest {

    @Test
    public void testAddCompound() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound manifest = client.addCompound(CompoundType.DATA, contents);
        Assert.assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(retrievedManifest.getManifestType(), ManifestConstants.COMPOUND);

        Collection<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toString(), retrievedManifest.toString(), true);
    }

    @Test
    public void testRetrieveCompoundFromFile() throws Exception {
        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        Compound manifest = client.addCompound(CompoundType.DATA, contents);
        assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);

        // Flush the internalStorage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.COMPOUND, retrievedManifest.getManifestType());

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

        Compound manifest = client.addCompound(CompoundType.DATA, contents);
        Manifest retrievedManifest = client.getManifest(manifest.getContentGUID());

        boolean isVerified = client.verifyManifest(client.getIdentity(), retrievedManifest);
        assertTrue(isVerified);
    }


}
