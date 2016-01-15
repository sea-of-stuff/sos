package sos.deserializers;

import com.google.gson.*;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.GUIDsha1;

import java.lang.reflect.Type;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentDeserializer implements JsonDeserializer<Content> {

    @Override
    public Content deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Content ret;

        JsonObject obj = json.getAsJsonObject();

        JsonElement jGUID = obj.get(ManifestConstants.CONTENT_KEY_GUID);
        String sGUID = jGUID.getAsString();
        GUID guid = new GUIDsha1(sGUID);

        JsonElement jLabel = obj.get(ManifestConstants.CONTENT_KEY_LABEL);
        if (jLabel != null) {
            String label = jLabel.getAsString();
            ret = new Content(label, guid);
        } else {
            ret = new Content(guid);
        }

        return ret;
    }
}
