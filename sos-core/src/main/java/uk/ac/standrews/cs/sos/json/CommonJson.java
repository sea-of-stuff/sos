package uk.ac.standrews.cs.sos.json;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class CommonJson {

    static IGUID GetGUID(JsonNode node, String key) throws GUIDGenerationException {

        String guid = node.get(key).textValue();
        return GUIDFactory.recreateGUID(guid);
    }

    static Set<IGUID> GetGUIDCollection(JsonNode node, String key) throws GUIDGenerationException {

        JsonNode nodes = node.get(key);
        Set<IGUID> retval = null;
        if (nodes != null && nodes.isArray()) {
            retval = new LinkedHashSet<>();
            for(final JsonNode aNode:nodes) {
                IGUID guid = GUIDFactory.recreateGUID(aNode.textValue());
                retval.add(guid);
            }
        }

        return retval;
    }

}
