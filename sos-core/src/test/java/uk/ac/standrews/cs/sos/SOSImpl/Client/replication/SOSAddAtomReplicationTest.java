package uk.ac.standrews.cs.sos.SOSImpl.Client.replication;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.HelperTest;

import java.io.InputStream;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAddAtomReplicationTest extends ClientReplicationTest {

    @Test
    public void testAddAtomFromStream() throws Exception {
        String testString = "first line and second line - replica";
        InputStream stream = HelperTest.StringToInputStream(testString);
        AtomBuilder builder = new AtomBuilder().setInputStream(stream);
        Atom manifest = client.addAtom(builder);
        assertNotNull(manifest.getContentGUID());
        assertEquals(manifest.getLocations().size(), 1);

        InputStream inputStream = client.getAtomContent(manifest);
        String resultString = HelperTest.InputStreamToString(inputStream);
        assertEquals(testString, resultString);

        stream.close();
        inputStream.close();

        // TODO
        // clear cache
        // force node to get data from replica node
    }
}
