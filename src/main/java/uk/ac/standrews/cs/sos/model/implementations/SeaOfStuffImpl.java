package uk.ac.standrews.cs.sos.model.implementations;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.managers.MemCache;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.components.Metadata;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate components
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffImpl implements SeaOfStuff {

    private Identity identity;
    private ManifestsManager manifestsManager;
    private SeaConfiguration configuration;

    public SeaOfStuffImpl(SeaConfiguration configuration, MemCache cache) throws KeyGenerationException, KeyLoadedException, IOException {
        this.configuration = configuration;

        identity = new IdentityImpl(configuration);
        manifestsManager = new ManifestsManager(configuration, cache);

        backgroundProcesses();
    }

    private void backgroundProcesses() {
        // TODO
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    @Override
    public AtomManifest addAtom(Collection<Location> locations)
            throws ManifestNotMadeException, ManifestSaveException, DataStorageException {

        AtomManifest manifest = ManifestFactory.createAtomManifest(configuration, locations);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public CompoundManifest addCompound(Collection<Content> contents)
            throws ManifestNotMadeException, ManifestSaveException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AssetManifest addAsset(Content content,
                                  GUID invariant,
                                  Collection<GUID> prevs,
                                  Collection<GUID> metadata)
            throws ManifestNotMadeException, ManifestSaveException {

        AssetManifest manifest = ManifestFactory.createAssetManifest(content, invariant, prevs, metadata, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public InputStream getAtomContent(AtomManifest atomManifest) {
        InputStream dataStream = null;
        Collection<Location> locations = atomManifest.getLocations();
        for(Location location:locations) {

            try {
                dataStream = DataStorage.getInputStreamFromLocation(location);
            } catch (SourceLocationException e) {
                continue;
            }

            if (dataStream != null) {
                break;
            }
        }

        return dataStream;
    }

    @Override
    public Manifest getManifest(GUID guid) throws UnknownGUIDException {
        Manifest manifest;
        try {
            manifest = manifestsManager.findManifest(guid);
        } catch (ManifestException e) {
            throw new UnknownGUIDException();
        }
        return manifest;
    }

    @Override
    public boolean verifyManifest(Manifest manifest) throws ManifestVerificationFailedException {
        boolean ret;
        try {
            ret = manifest.verify();
        } catch (GuidGenerationException e) {
            throw new ManifestVerificationFailedException();
        }

        return ret;
    }

    @Override
    public Collection<GUID> findManifestByType(String type) {
        return manifestsManager.findManifestsByType(type);
    }

    @Override
    public Collection<GUID> findManifestByLabel(String label) {
        return manifestsManager.findManifestsThatMatchLabel(label);
    }

    @Override
    public void findManifests(Metadata metadata) {
        // - look at manifests manager
        // - having a look at the redis storage would be very helpful!
        // - need to define what metadata is
        throw new NotImplementedException();
    }
}
