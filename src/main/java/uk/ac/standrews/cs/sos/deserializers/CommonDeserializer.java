package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonDeserializer {

    public IGUID getGUID(JsonObject object, String key) throws GUIDGenerationException {
        String sGUID = object.get(key).getAsString();
        return GUIDFactory.recreateGUID(sGUID);
    }

    public Collection<IGUID> getGUIDCollection(JsonObject object, String key) throws GUIDGenerationException {
        JsonArray jGUIDs = object.getAsJsonArray(key);
        Collection<IGUID> guids = new ArrayList<>();
        fillGUIDCollection(jGUIDs, guids);

        return guids;
    }

    private void fillGUIDCollection(JsonArray jsonArray, Collection<IGUID> collection) throws GUIDGenerationException {
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String sElement = jsonArray.get(i).getAsString();
                IGUID guid = GUIDFactory.recreateGUID(sElement);
                collection.add(guid);
            }
        }
    }

}
