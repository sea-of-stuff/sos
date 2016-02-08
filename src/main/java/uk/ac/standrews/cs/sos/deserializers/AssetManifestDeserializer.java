package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestDeserializer implements JsonDeserializer<AssetManifest> {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Content.class, new ContentDeserializer()).create();
    private static final int CONTENT_INDEX = 1;

    @Override
    public AssetManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String signature = obj.get(ManifestConstants.KEY_SIGNATURE).getAsString();

        String sInvariant = obj.get(ManifestConstants.KEY_INVARIANT).getAsString();
        GUID invariant = new GUIDsha1(sInvariant);

        JsonElement jContents = obj.get(ManifestConstants.KEY_CONTENTS);
        Content content = gson.fromJson(jContents, Content.class);

        JsonArray jPrevs = obj.getAsJsonArray(ManifestConstants.KEY_PREVIOUS_GUID);
        Collection<GUID> prevs = new ArrayList<>();
        fillGUIDCollection(jPrevs, prevs);

        JsonArray jMeta = obj.getAsJsonArray(ManifestConstants.KEY_METADATA_GUID);
        Collection<GUID> metadata = new ArrayList<>();
        fillGUIDCollection(jMeta, metadata);

        AssetManifest manifest = null;
        try {
            manifest = ManifestFactory.createAssetManifest(content, invariant, prevs, metadata, signature);
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        }

        return manifest;
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
