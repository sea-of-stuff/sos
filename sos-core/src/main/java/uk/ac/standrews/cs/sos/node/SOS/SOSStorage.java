package uk.ac.standrews.cs.sos.node.SOS;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.Roles;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSStorage extends SOSCommon {

    public SOSStorage(SeaConfiguration configuration, ManifestsManager manifestsManager, Identity identity, Roles role) {
        super(configuration, manifestsManager, identity, role);
    }

    @Override
    public Manifest addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        return null;
    }

    @Override
    public InputStream getAtomContent(Atom atom) {
        return null;
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException {
        return false;
    }

    @Override
    public Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException {
        return null;
    }

    @Override
    public Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException {
        return null;
    }

    @Override
    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        return DataStorageHelper.persistAtomAndUpdateLocationBundles(configuration, location, bundles);
    }

    @Override
    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        return DataStorageHelper.persistAtomAndUpdateLocationBundles(configuration, inputStream, bundles);
    }
}
