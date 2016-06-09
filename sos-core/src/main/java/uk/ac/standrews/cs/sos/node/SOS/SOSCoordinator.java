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
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestsManager;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCoordinator extends SOSCommon {

    public SOSCoordinator(Configuration configuration, ManifestsManager manifestsManager, Identity identity) {
        super(configuration, manifestsManager, identity);
    }

    @Override
    public Atom addAtom(Location location) throws DataStorageException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Atom addAtom(InputStream inputStream) throws DataStorageException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents) throws ManifestNotMadeException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Version addVersion(IGUID content, IGUID invariant, Collection<IGUID> prevs, Collection<IGUID> metadata) throws ManifestNotMadeException, ManifestPersistException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws DataStorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Manifest addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsManager.addManifest(manifest);
        
        return manifest;
    }

    @Override
    public InputStream getAtomContent(Atom atom) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        // TODO - might have to contact other coordinators!
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

    // TODO - other find methods?
}
