package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.lang.reflect.Type;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContentDeserializer extends CommonDeserializer implements JsonDeserializer<Content> {

    @Override
    public Content deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        try {
            IGUID guid = getGUID(obj, ManifestConstants.CONTENT_KEY_GUID);
            JsonElement jLabel = obj.get(ManifestConstants.CONTENT_KEY_LABEL);
            Content ret;
            if (jLabel != null) {
                String label = jLabel.getAsString();
                ret = new Content(label, guid);
            } else {
                ret = new Content(guid);
            }

            return ret;
        } catch (GUIDGenerationException e) {
            throw new JsonParseException("Could not recreated GUIDs");
        }
    }
}