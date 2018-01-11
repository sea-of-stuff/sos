package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;

import java.util.LinkedHashSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddRetrieveManifestTest extends ManifestDataServiceTest {

    @Test
    public void addManifestTest() throws ManifestPersistException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        // No exception
    }

    @Test
    public void addManifestAndRetrieveTest() throws ManifestPersistException, ManifestNotFoundException {

        IGUID guid = GUIDFactory.generateRandomGUID();
        Manifest manifest = ManifestFactory.createAtomManifest(guid, new LinkedHashSet<>());

        manifestsDataService.addManifest(manifest);
        Manifest manifestRetrieved = manifestsDataService.getManifest(guid);
        assertNotNull(manifestRetrieved);
        assertEquals(manifestRetrieved, manifest);
    }
}
