package uk.ac.standrews.cs.sos.deserializers;

import com.google.gson.*;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.model.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetManifestDeserializer extends CommonDeserializer implements JsonDeserializer<AssetManifest> {

    @Override
    public AssetManifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        try {
            IGUID invariant = getGUID(obj, ManifestConstants.KEY_INVARIANT);
            IGUID version = getGUID(obj, ManifestConstants.KEY_VERSION);
            IGUID content = GUIDFactory.recreateGUID(obj.get(ManifestConstants.KEY_CONTENT_GUID).getAsString());
            Collection<IGUID> prevs = getGUIDCollection(obj, ManifestConstants.KEY_PREVIOUS_GUID);
            Collection<IGUID> metadata = getGUIDCollection(obj, ManifestConstants.KEY_METADATA_GUID);
            String signature = obj.get(ManifestConstants.KEY_SIGNATURE).getAsString();

            return new AssetManifest(invariant, version, content, prevs, metadata, signature);
        } catch (GUIDGenerationException e) {
            throw new JsonParseException("Could not recreated GUIDs");
        }

    }


}
