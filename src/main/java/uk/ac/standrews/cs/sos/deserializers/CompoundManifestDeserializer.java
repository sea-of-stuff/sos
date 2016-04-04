package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.utils.GUID;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifestDeserializer extends CommonDeserializer implements JsonDeserializer<CompoundManifest> {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Content.class, new ContentDeserializer()).create();

    @Override
    public CompoundManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        GUID contentGUID = getGUID(obj, ManifestConstants.KEY_CONTENT_GUID);
        String signature = obj.get(ManifestConstants.KEY_SIGNATURE).getAsString();

        String compoundTypeString = obj.get(ManifestConstants.KEY_COMPOUND_TYPE).getAsString();
        CompoundType compoundType = CompoundType.valueOf(compoundTypeString);

        JsonArray jContents = obj.getAsJsonArray(ManifestConstants.KEY_CONTENTS);
        Collection<Content> contents = new ArrayList<>();
        for(int i = 0; i < jContents.size(); i++) {
            Content content = gson.fromJson(jContents.get(i), Content.class);
            contents.add(content);
        }

        return new CompoundManifest(compoundType, contentGUID, contents, signature);
    }
}
