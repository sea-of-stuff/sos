package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ResolvePathTest extends ManifestDataServiceTest {

    @Test
    public void resolvePath_0() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);

        Manifest retval = manifestsDataService.resolvePath(guid.toMultiHash());
        assertNotNull(retval);
        assertEquals(manifest.guid(), retval.guid());
    }

    @Test
    public void resolvePath_1() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content hello = new ContentImpl("hello", atom.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/" + compound.guid().toMultiHash() + "/hello");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_2() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content hello = new ContentImpl("hello", atom.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_3() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content hello = new ContentImpl("hello", atom.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/" + atom.guid().toMultiHash());
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }


    @Test
    public void resolvePath_4() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);

        Content hello = new ContentImpl("hello", compound_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello/world");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_5() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);

        Content hello = new ContentImpl("hello", compound_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello/" + atom.guid().toMultiHash());
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_6() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);

        Content hello = new ContentImpl("hello", compound_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/" + compound_0.guid().toMultiHash() + "/world");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_7() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);
        Version version_0 = ManifestFactory.createVersionManifest(compound_0.guid(), null, null, null, null);

        Content hello = new ContentImpl("hello", version_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(version_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello/world");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_8() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);
        Version version_0 = ManifestFactory.createVersionManifest(compound_0.guid(), null, null, null, null);

        Content hello = new ContentImpl("hello", version_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(version_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/" + version_0.guid().toMultiHash() + "/world");
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_9() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);
        Version version_0 = ManifestFactory.createVersionManifest(compound_0.guid(), null, null, null, null);

        Content hello = new ContentImpl("hello", version_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(version_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello/" + atom.guid().toMultiHash());
        assertNotNull(retval);
        assertEquals(atom.guid(), retval.guid());
    }

    @Test
    public void resolvePath_10_partial() throws ManifestPersistException, ManifestNotFoundException, ManifestNotMadeException {

        Manifest atom = ManifestFactory.createAtomManifest(GUIDFactory.generateRandomGUID(), new LinkedHashSet<>());

        Content world = new ContentImpl("world", atom.guid());
        Set<Content> contents_0 = new LinkedHashSet<>();
        contents_0.add(world);

        Compound compound_0 = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents_0, null);
        Version version_0 = ManifestFactory.createVersionManifest(compound_0.guid(), null, null, null, null);

        Content hello = new ContentImpl("hello", version_0.guid());
        Set<Content> contents = new LinkedHashSet<>();
        contents.add(hello);

        Compound compound = ManifestFactory.createCompoundManifest(CompoundType.COLLECTION, contents, null);
        Version version = ManifestFactory.createVersionManifest(compound.guid(), null, null, null, null);

        manifestsDataService.addManifest(atom);
        manifestsDataService.addManifest(compound_0);
        manifestsDataService.addManifest(version_0);
        manifestsDataService.addManifest(compound);
        manifestsDataService.addManifest(version);

        Manifest retval = manifestsDataService.resolvePath(version.guid().toMultiHash() + "/hello");
        assertNotNull(retval);
        assertEquals(version_0.guid(), retval.guid());
    }

}
