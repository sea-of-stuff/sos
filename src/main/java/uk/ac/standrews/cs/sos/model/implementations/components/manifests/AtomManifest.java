package uk.ac.standrews.cs.sos.model.implementations.components.manifests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.model.implementations.locations.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;

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

    private Collection<LocationBundle> locations;

    public AtomManifest() {
        super(ManifestConstants.ATOM);
    }

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param locations
     */
    protected AtomManifest(GUID guid, Collection<LocationBundle> locations) throws ManifestNotMadeException {
        super(ManifestConstants.ATOM);
        this.contentGUID = guid;
        this.locations = locations;

        //make();
    }

    /**
     * Gets a collection of locations for this atom.
     *
     * @return locations of this atom.
     */
    public Collection<LocationBundle> getLocations() {
        return locations;
    }

    public void setLocations(Collection<LocationBundle> locations) {
        if (this.locations == null)
            this.locations = locations;
    }

    @Override
    public boolean verify(Identity identity) throws GuidGenerationException {
        if (contentGUID == null)
            return false;

        for(LocationBundle location:locations) {
            InputStream dataStream;
            try {
                dataStream = DataStorage.getInputStreamFromLocation(location.getLocations()[0]); // FIXME - assume only one location
            } catch (SourceLocationException | URISyntaxException | IOException e) {
                continue;
            }

            if (!verifyStream(dataStream)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                !locations.isEmpty() &&
                isGUIDValid(contentGUID);
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

    private boolean verifyStream(InputStream inputStream) throws GuidGenerationException {
        return inputStream != null &&
                contentGUID.equals(generateGUID(inputStream));
    }


}
