package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.json.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.json.AtomManifestSerializer;
import uk.ac.standrews.cs.sos.model.datastore.StorageHelper;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.io.IOException;
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
@JsonSerialize(using = AtomManifestSerializer.class)
@JsonDeserialize(using = AtomManifestDeserializer.class)
public class AtomManifest extends BasicManifest implements Atom {

    final private Collection<LocationBundle> locations;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public AtomManifest(IGUID guid, Collection<LocationBundle> locations) {
        super(ManifestConstants.ATOM);
        this.contentGUID = guid;
        this.locations = locations;
    }

    /**
     * Gets a collection of locations for this atom.
     *
     * @return locations of this atom.
     */
    @Override
    public Collection<LocationBundle> getLocations() {
        return locations;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                !locations.isEmpty() &&
                isGUIDValid(contentGUID);
    }

    @Override
    public boolean verify(Identity identity) throws GUIDGenerationException {
        if (contentGUID == null)
            return false;

        for(LocationBundle location:locations) {
            try (InputStream dataStream =
                         StorageHelper.getInputStreamFromLocation(location.getLocation())) {
                if (!verifyStream(dataStream)) {
                    return false;
                }
            } catch (SourceLocationException | IOException e) {}
        }

        return true;
    }

    private boolean verifyStream(InputStream inputStream) throws GUIDGenerationException {
        return inputStream != null &&
                contentGUID.equals(GUIDFactory.generateGUID(inputStream));
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
