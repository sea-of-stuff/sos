package uk.ac.standrews.cs.sos.model.implementations;

import org.skyscreamer.jsonassert.JSONAssert;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffAddCompoundTest extends SeaOfStuffGeneralTest {

    @Test
    public void testAddCompound() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest manifest = model.addCompound(contents);
        assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.COMPOUND, retrievedManifest.getManifestType());

        Collection<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testRetrieveCompoundFromFile() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest manifest = model.addCompound(contents);
        assertEquals(manifest.getManifestType(), ManifestConstants.COMPOUND);

        // Flush the storage, so to force the manifest to be retrieved from file.
        index.flushDB();

        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());
        assertEquals(ManifestConstants.COMPOUND, retrievedManifest.getManifestType());

        Collection<Content> retrievedContents = ((CompoundManifest) retrievedManifest).getContents();
        Iterator<Content> iterator = retrievedContents.iterator();
        assertEquals(cat, iterator.next());

        JSONAssert.assertEquals(manifest.toJSON().toString(), retrievedManifest.toJSON().toString(), true);
    }

    @Test
    public void testAddCompoundAndVerify() throws Exception {
        Content cat = new Content("cat", new GUIDsha1("123"));
        Collection<Content> contents = new ArrayList<>();
        contents.add(cat);

        CompoundManifest manifest = model.addCompound(contents);
        Manifest retrievedManifest = model.getManifest(manifest.getContentGUID());

        boolean isVerified = model.verifyManifest(model.getIdentity(), retrievedManifest);
        assertTrue(isVerified);
    }


}
