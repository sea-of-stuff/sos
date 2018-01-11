package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.LinkedHashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteManifestTest extends ManifestDataServiceTest {

    @Test
    public void deleteAddedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        // No exception is thrown
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteAddedManifestWithRetrieveTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.getManifest(guid);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteDeletedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.delete(guid);
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteManifestNotAddedTest() throws ManifestNotFoundException {

        manifestsDataService.delete(GUIDFactory.generateRandomGUID());
    }

    @Test
    public void deleteLocationOfDeletedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.deleteLocalLocation(guid); // Does not throws any exception
    }

    @Test
    public void deleteAddedVersionManifestTest() throws Exception {

        Manifest manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(manifest.guid());
        // No exception is thrown
    }

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteAddedVersionManifestWithRetrieveTest() throws Exception {

        Manifest manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(manifest.guid());
        manifestsDataService.getManifest(manifest.guid());
    }

    @Test (expectedExceptions = HEADNotFoundException.class)
    public void checkHeadIsDeletedTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        manifestsDataService.getHead(manifest.invariant());

        manifestsDataService.delete(manifest.guid());
        manifestsDataService.getHead(manifest.invariant());
    }

}
