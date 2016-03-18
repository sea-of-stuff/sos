package uk.ac.standrews.cs.sos.model.implementations.manifests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;

/**
 * Manifest describing an Atom.
 *
 * <p>
 * Manifest's GUID - GUID <br>
 * ManifestType - ATOM <br>
 * Locations - collection of locations <br>
 * Content - GUID Content
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends BasicManifest {

    final private Collection<LocationBundle> locations;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public AtomManifest(GUID guid, Collection<LocationBundle> locations) {
        super(ManifestConstants.ATOM);
        this.contentGUID = guid;
        this.locations = locations;
    }

    /**
     * Gets a collection of locations for this atom.
     *
     * @return locations of this atom.
     */
    public Collection<LocationBundle> getLocations() {
        return locations;
    }

    @Override
    public JsonObject toJSON() {
        JsonObject obj = super.toJSON();

        JsonArray array = new JsonArray();
        Collection<LocationBundle> locations = getLocations();
        for(LocationBundle location:locations)
            array.add(location.toJSON());

        obj.add(ManifestConstants.KEY_LOCATIONS, array);
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, getContentGUID().toString());

        return obj;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                !locations.isEmpty() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public boolean verify(Identity identity) throws GuidGenerationException {
        if (contentGUID == null)
            return false;

        for(LocationBundle location:locations) {
            InputStream dataStream;
            try {
                dataStream = DataStorageHelper.getInputStreamFromLocation(location.getLocation());
            } catch (SourceLocationException e) {
                continue;
            }

            if (!verifyStream(dataStream)) {
                return false;
            }
        }

        return true;
    }

    private boolean verifyStream(InputStream inputStream) throws GuidGenerationException {
        return inputStream != null &&
                contentGUID.equals(GUID.generateGUID(inputStream));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomManifest that = (AtomManifest) o;
        return Objects.equals(locations, that.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }

}
