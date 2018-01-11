package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;

import java.util.LinkedHashSet;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteManifestTest extends ManifestDataServiceTest {

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteAddedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

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

    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void deleteLocationOfDeletedManifestTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        manifestsDataService.delete(guid);
        manifestsDataService.deleteLocalLocation(guid);
    }
}
