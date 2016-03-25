package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.utils.GUID;
import uk.ac.standrews.cs.utils.GUIDsha1;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonDeserializer {

    public GUID getGUID(JsonObject object, String key) {
        String sGUID = object.get(key).getAsString();
        return new GUIDsha1(sGUID);
    }

    public Collection<GUID> getGUIDCollection(JsonObject object, String key) {
        JsonArray jGUIDs = object.getAsJsonArray(key);
        Collection<GUID> guids = new ArrayList<>();
        fillGUIDCollection(jGUIDs, guids);

        return guids;
    }

    private void fillGUIDCollection(JsonArray jsonArray, Collection<GUID> collection) {
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String sElement = jsonArray.get(i).getAsString();
                GUID guid = new GUIDsha1(sElement);
                collection.add(guid);
            }
        }
    }

}
