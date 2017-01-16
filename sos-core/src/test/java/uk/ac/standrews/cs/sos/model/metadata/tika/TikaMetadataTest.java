package uk.ac.standrews.cs.sos.model.metadata.tika;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;

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
        TikaMetadata output = test.processData(data);

        JsonNode node = JSONHelper.JsonObjMapper().readTree(output.toString());
        assertTrue(node.has("GUID"));
        assertTrue(node.has("Properties"));
        assertEquals(node.get("Properties").size(), 5);
    }
}