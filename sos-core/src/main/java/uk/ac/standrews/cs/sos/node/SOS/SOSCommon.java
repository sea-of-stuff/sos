package uk.ac.standrews.cs.sos.node.SOS;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.Roles;
import uk.ac.standrews.cs.sos.interfaces.node.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class SOSCommon implements SeaOfStuff {

    protected Identity identity;
    protected ManifestsManager manifestsManager;
    final protected SeaConfiguration configuration;

    private Roles role;

    public SOSCommon(SeaConfiguration configuration, ManifestsManager manifestsManager, Identity identity, Roles role) {
        this.configuration = configuration;
        this.manifestsManager = manifestsManager;
        this.identity = identity;

        this.role = role;
    }

    @Override
    public Identity getIdentity() {
        return this.identity;
    }

    @Override
    public Roles getRoleMask() {
        return role;
    }

    @Override
    public Atom addAtom(Location location) throws DataStorageException, ManifestPersistException {

        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(new ProvenanceLocationBundle(location));

        IGUID guid = store(location, bundles);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Atom addAtom(InputStream inputStream)
            throws ManifestPersistException, DataStorageException {

        Collection<LocationBundle> bundles = new ArrayList<>();
        IGUID guid = store(inputStream, bundles);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(type, contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Version addVersion(IGUID content,
                              IGUID invariant,
                              Collection<IGUID> prevs,
                              Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException {

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    protected abstract IGUID store(Location location, Collection<LocationBundle> bundles) throws DataStorageException;

    protected abstract IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws DataStorageException;
}
