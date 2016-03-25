package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.utils.GUID;
import uk.ac.standrews.cs.utils.GUIDsha1;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestDeserializer extends CommonDeserializer implements JsonDeserializer<AssetManifest> {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Content.class, new ContentDeserializer()).create();

    @Override
    public AssetManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        GUID invariant = getGUID(obj, ManifestConstants.KEY_INVARIANT);
        GUID version = getGUID(obj, ManifestConstants.KEY_VERSION);

        GUID content = new GUIDsha1(obj.get(ManifestConstants.KEY_CONTENT_GUID).getAsString());

        Collection<GUID> prevs = getGUIDCollection(obj, ManifestConstants.KEY_PREVIOUS_GUID);
        Collection<GUID> metadata = getGUIDCollection(obj, ManifestConstants.KEY_METADATA_GUID);

        String signature = obj.get(ManifestConstants.KEY_SIGNATURE).getAsString();

        return new AssetManifest(invariant, version, content, prevs, metadata, signature);
    }


}
