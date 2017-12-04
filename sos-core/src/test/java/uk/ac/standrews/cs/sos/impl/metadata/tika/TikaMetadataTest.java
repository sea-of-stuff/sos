package uk.ac.standrews.cs.sos.impl.metadata.tika;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TikaMetadataTest {

    @Test
    public void basicJSONTest() throws MetadataException, IOException {

        TikaMetadataEngine test = new TikaMetadataEngine();

        Data data = new StringData("just some text in a string");
        Metadata output = test.processData(data, null);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(output.toString());
        assertTrue(node.has(JSONConstants.KEY_GUID));
        assertTrue(node.has(JSONConstants.KEY_META_PROPERTIES));
        assertEquals(node.get(JSONConstants.KEY_META_PROPERTIES).size(), 5);
    }
}
