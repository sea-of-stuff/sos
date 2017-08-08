package uk.ac.standrews.cs.sos.impl.manifests;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
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
    public Data getData() throws IOException {

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
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() throws IOException {
        return getData().getInputStream();
    }

    @Override
    public boolean verifySignature(Role role) throws SignatureException {

        return false;
    }

    /**
     * Verifies the integrity of all the available sources
     *
     * @return true if the data at all locations for this atom matches the guid of the manifest
     */
    @Override
    public boolean verifyIntegrity() {

        for(LocationBundle location:locations) {
            try (Data data = LocationUtility.getDataFromLocation(location.getLocation())) {

                if (!guid().equals(GUIDFactory.generateGUID(ALGORITHM.SHA256, data.getInputStream()))) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifies the integrity for a specific location
     * @param locationBundle
     * @return
     */
    public boolean verifyIntegrity(LocationBundle locationBundle) {

        try (Data data = LocationUtility.getDataFromLocation(locationBundle.getLocation())) {

            if (!guid().equals(GUIDFactory.generateGUID(ALGORITHM.SHA256, data.getInputStream()))) {
                return false;
            }

        } catch (Exception e) {
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
