package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.json.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.json.AtomManifestSerializer;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.CompressionAlgorithms;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

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

    final private Set<LocationBundle> locations;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid
     * @param locations
     */
    public AtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(ManifestType.ATOM);
        this.contentGUID = guid;
        this.locations = locations;
    }

    /**
     * Gets a collection of locations for this atom.
     *
     * @return locations of this atom.
     */
    @Override
    public Set<LocationBundle> getLocations() {
        return locations;
    }

    @Override
    public InputStream getData() {
        InputStream dataStream = null;
        for (LocationBundle location : locations) {

            dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation());

            if (!(dataStream instanceof NullInputStream)) {
                break;
            }
        }

        return dataStream;
    }

    @Override
    public CompressionAlgorithms getCompressionAlgorithm() {
        return CompressionAlgorithms.NONE;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && !locations.isEmpty() && isGUIDValid(contentGUID);
    }

    @Override
    public IGUID guid() {
        return contentGUID;
    }

    @Override
    public boolean verifySignature(Role role) throws SignatureException {
        if (contentGUID == null || contentGUID.isInvalid())
            return false;

        for(LocationBundle location:locations) {
            try (InputStream dataStream = LocationUtility.getInputStreamFromLocation(location.getLocation())) {

                if (!verifyStream(dataStream)) {
                    return false;
                }

            } catch (GUIDGenerationException| IOException e) {
                throw new SignatureException("Unable to verify Atom Manifest", e);
            }
        }

        return true;
    }

    private boolean verifyStream(InputStream inputStream) throws GUIDGenerationException {
        return inputStream != null && contentGUID.equals(GUIDFactory.generateGUID(inputStream));
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
