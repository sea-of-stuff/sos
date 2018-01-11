package uk.ac.standrews.cs.sos.impl.services.ManifestData;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.ManifestUtils;

import java.util.LinkedHashSet;

import static org.testng.Assert.*;

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

    @Test
    public void addVersionManifestAndRetrieveTest() throws Exception {

        Manifest manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        Manifest manifestRetrieved = manifestsDataService.getManifest(manifest.guid());
        assertNotNull(manifestRetrieved);
        assertEquals(manifestRetrieved, manifest);
    }

    @Test
    public void addVersionManifestAndRetrieveHeadRefTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        IGUID head = manifestsDataService.getHead(manifest.invariant());
        assertNotNull(head);
        assertFalse(head.isInvalid());
    }

    @Test
    public void addVersionManifestAndRetrieveHeadManifestTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        IGUID head = manifestsDataService.getHead(manifest.invariant());

        Manifest manifestRetrieved = manifestsDataService.getManifest(head);
        assertNotNull(manifestRetrieved);
        assertEquals(manifestRetrieved, manifest);
    }

    @Test
    public void addVersionManifestAndRetrieveTipRefTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        IGUID tip = manifestsDataService.getTips(manifest.invariant()).iterator().next();
        assertNotNull(tip);
        assertFalse(tip.isInvalid());
    }

    @Test
    public void addVersionManifestAndRetrieveTipManifestTest() throws Exception {

        Version manifest = ManifestUtils.createDummyVersion();

        manifestsDataService.addManifest(manifest);
        IGUID tip = manifestsDataService.getTips(manifest.invariant()).iterator().next();

        Manifest manifestRetrieved = manifestsDataService.getManifest(tip);
        assertNotNull(manifestRetrieved);
        assertEquals(manifestRetrieved, manifest);
    }

}
