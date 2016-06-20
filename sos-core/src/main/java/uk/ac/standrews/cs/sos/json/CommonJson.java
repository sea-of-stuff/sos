package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonJson {

    public static IGUID GetGUID(JsonNode node, String key) throws GUIDGenerationException {
        String guid = node.get(key).textValue();
        return GUIDFactory.recreateGUID(guid);
    }

    public static Collection<IGUID> GetGUIDCollection(JsonNode node, String key) throws GUIDGenerationException {
        JsonNode nodes = node.get(key);
        Collection<IGUID> retval = null;
        if (nodes != null && nodes.isArray()) {
            retval = new ArrayList<>();
            for(final JsonNode aNode:nodes) {
                IGUID guid = GUIDFactory.recreateGUID(aNode.textValue());
                retval.add(guid);
            }
        }

        return retval;
    }

}
