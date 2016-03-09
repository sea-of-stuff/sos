package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestDeserializer extends CommonDeserializer implements JsonDeserializer<CompoundManifest> {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Content.class, new ContentDeserializer()).create();

    @Override
    public CompoundManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        GUID contentGUID = getGUID(obj, ManifestConstants.KEY_CONTENT_GUID);

        String signature = obj.get(ManifestConstants.KEY_SIGNATURE).getAsString();

        JsonArray jContents = obj.getAsJsonArray(ManifestConstants.KEY_CONTENTS);
        Collection<Content> contents = new ArrayList<>();
        for(int i = 0; i < jContents.size(); i++) {
            Content content = gson.fromJson(jContents.get(i), Content.class);
            contents.add(content);
        }

        CompoundManifest manifest = new CompoundManifest(contentGUID, contents, signature);
        return manifest;
    }
}
