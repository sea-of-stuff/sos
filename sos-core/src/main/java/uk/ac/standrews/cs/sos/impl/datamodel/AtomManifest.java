package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.CompressionAlgorithms;
import uk.ac.standrews.cs.sos.model.ManifestType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

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
public class AtomManifest extends BasicManifest implements Atom {

    final private Set<LocationBundle> locations;

    /**
     * Creates a valid atom manifest given an atom.
     *
     * @param guid of the atom
     * @param locations where the atom is stored
     */
    public AtomManifest(IGUID guid, Set<LocationBundle> locations) {
        super(ManifestType.ATOM);
        this.guid = guid;
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
    public Data getData() {

        for (LocationBundle location : getLocations()) {

            Data data = LocationUtility.getDataFromLocation(location.getLocation());

            if (!(data instanceof EmptyData)) {
                return data;
            }
        }

        return new EmptyData();
    }

    @Override
    public CompressionAlgorithms getCompressionAlgorithm() {
        return CompressionAlgorithms.NONE;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && !locations.isEmpty() && isGUIDValid(guid);
    }

    @Override
    public InputStream contentToHash() throws IOException {
        return getData().getInputStream();
    }

    /**
     * Verifies the integrity of all the available sources
     *
     * @return true if the data at all locations for this atom matches the guid of the manifest
     */
    @Override
    public boolean verifyIntegrity() {

        for(LocationBundle location:locations) {

            boolean verified = verifyIntegrity(location);
            if (!verified) return false;
        }

        return true;
    }

    /**
     * Verifies the integrity for a specific location
     * @param locationBundle where the data to verify is
     * @return if the data integrity was verified
     */
    @Override
    public boolean verifyIntegrity(LocationBundle locationBundle) {

        try (Data data = LocationUtility.getDataFromLocation(locationBundle.getLocation())) {

            IGUID generatedGUID = GUIDFactory.generateGUID(GUID_ALGORITHM, data.getInputStream());
            if (generatedGUID.isInvalid() || !guid().equals(generatedGUID)) {
                return false;
            }

        } catch (IOException | GUIDGenerationException e) {
            return false;
        }

        return true;
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
