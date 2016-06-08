package uk.ac.standrews.cs.sos.node.SOS.Storage;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.utils.Helper;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomTest extends StorageTest {

    @Test
    public void testRetrieveAtomData() throws Exception {
        Location location = Helper.createDummyDataFile(configuration);
        Atom manifest = model.addAtom(location);
        assertEquals(manifest.getManifestType(), ManifestConstants.ATOM);

        InputStream inputStream = model.getAtomContent(manifest);

        assertTrue(IOUtils.contentEquals(location.getSource(), inputStream));
    }
}
