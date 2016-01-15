package sos.model.implementations.components.manifests;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sos.exceptions.GuidGenerationException;
import sos.exceptions.ManifestNotMadeException;
import sos.exceptions.SourceLocationException;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;

import java.io.IOException;
import java.io.InputStream;
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

    private GUID contentGUID;
    private Collection<Location> locations;
    private int contentSize;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param locations
     */
    protected AtomManifest(Collection<Location> locations) throws ManifestNotMadeException {
        super(ManifestConstants.ATOM);
        this.locations = locations;
        this.contentSize = 0; // TODO - initialise contentSize properly if possible
        make();
    }

    // FIXME - REMOVEME - maybe not needed - see gson serialization
    protected AtomManifest(GUID contentGUID, Collection<Location> locations) {
        super(ManifestConstants.ATOM);
        this.contentGUID = contentGUID;
        this.locations = locations;
        this.contentSize = 0; // TODO - initialise contentSize properly if possible
    }

    /**
     * Gets a collection of locations for this atom.
     *
     * @return locations of this atom.
     */
    public Collection<Location> getLocations() {
        return locations;
    }

    @Override
    public boolean verify() throws GuidGenerationException {
        if (contentGUID == null)
            return false;

        for(Location location:locations) {
            InputStream dataStream = null;
            try {
                dataStream = tryLocation(location);
            } catch (SourceLocationException e) {
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
        Collection<Location> locations = getLocations();
        for(Location location:locations)
            array.add(location.toString());

        obj.add(ManifestConstants.KEY_LOCATIONS, array);
        obj.addProperty(ManifestConstants.KEY_CONTENT_GUID, getContentGUID().toString());

        return obj;
    }

    @Override
    public GUID getContentGUID() {
        return this.contentGUID;
    }

    private void make() throws ManifestNotMadeException {
        try {
            contentGUID = generateContentGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }
    }

    private GUID generateContentGUID() throws GuidGenerationException {
        for(Location location:locations) {
            InputStream dataStream = null;
            try {
                dataStream = tryLocation(location);
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                contentGUID = generateGUID(dataStream);
                break; // Assume that all other locations point to the same source.
            }
        }

        if (contentGUID == null)
            throw new GuidGenerationException("All locations failed to return data");

        return contentGUID;
    }

    /**
     *
     * @return
     */
    private InputStream tryLocation(Location location) throws SourceLocationException {

        InputStream stream = null;
        try {
            stream = location.getSource();
        } catch (IOException e) {
            throw new SourceLocationException(location.getLocationPath().toString());
        }

        return stream;
    }

    private boolean verifyStream(InputStream inputStream) throws GuidGenerationException {
        return inputStream != null &&
                contentGUID.equals(generateGUID(inputStream));
    }

}
