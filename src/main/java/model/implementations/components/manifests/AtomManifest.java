package model.implementations.components.manifests;

import model.exceptions.GuidGenerationException;
import model.exceptions.ManifestNotMadeException;
import model.exceptions.SourceLocationException;
import model.implementations.utils.GUID;
import model.implementations.utils.Location;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Manifest describing an Atom.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - ATOM <br>
 * Locations - list of locations <br>
 * Content - GUID Content
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends BasicManifest {

    private GUID contentGUID;
    private Collection<Location> locations;

    /**
     * Creates a partially valid atom manifest given an atom.
     *
     * @param locations
     */
    protected AtomManifest(Collection<Location> locations) throws ManifestNotMadeException {
        super(ManifestConstants.ATOM);
        this.locations = locations;

        make();
    }

    private void make() throws ManifestNotMadeException {
        try {
            contentGUID = generateContentGUID();
        } catch (GuidGenerationException e) {
            throw new ManifestNotMadeException();
        }

        try {
            generateManifestGUID();
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

    public GUID getGUIDContent() {
        return contentGUID;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    /**
     * Verify that the sources at all locations match the content GUID.
     *
     * @return
     */
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

    private boolean verifyStream(InputStream inputStream) throws GuidGenerationException {
        return inputStream != null &&
                contentGUID == generateGUID(inputStream);
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                !locations.isEmpty() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = super.toJSON();

        obj.put(ManifestConstants.KEY_LOCATIONS, getLocations());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, getGUIDContent());

        return obj;
    }

    @Override
    protected String generateManifestToHash() {
        JSONObject obj = new JSONObject();

        obj.put(ManifestConstants.KEY_TYPE, this.getManifestType());
        obj.put(ManifestConstants.KEY_CONTENT_GUID, contentGUID);

        return obj.toString();
    }

}
