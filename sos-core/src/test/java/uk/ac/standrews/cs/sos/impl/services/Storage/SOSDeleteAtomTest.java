package uk.ac.standrews.cs.sos.impl.services.Storage;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteAtomTest extends StorageServiceTest {

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomDataTest() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT)
                .setSetLocationAndProvenance(false);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.deleteAtom(manifest.guid());
        storageService.getAtomContent(manifest);
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomAlreadyDeletedTest() throws Exception {
        Location location = HelperTest.createDummyDataFile(localStorage);

        AtomBuilder builder = new AtomBuilder()
                .setLocation(location)
                .setBundleType(BundleTypes.PERSISTENT)
                .setSetLocationAndProvenance(false);
        Atom manifest = storageService.addAtom(builder);
        assertEquals(manifest.getType(), ManifestType.ATOM);

        storageService.deleteAtom(manifest.guid());
        storageService.deleteAtom(manifest.guid());
    }

    @Test (expectedExceptions = AtomNotFoundException.class)
    public void deleteAtomNotAddedTest() throws Exception {

        storageService.deleteAtom(GUIDFactory.generateRandomGUID());
    }

}
