package uk.ac.standrews.cs.sos.model.implementations;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.GuidGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.managers.Index;
import uk.ac.standrews.cs.sos.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    public SeaOfStuffImpl(SeaConfiguration configuration, Index index) throws KeyGenerationException, KeyLoadedException, IOException {
        this.configuration = configuration;

        identity = new IdentityImpl(configuration);
        manifestsManager = new ManifestsManager(configuration, index);

        backgroundProcesses();
        registerSOSProtocol();
    }

    private void registerSOSProtocol() {
        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory());
        } catch (Error e) {
            // TODO - Error is thrown if the factory has already been setup for the JVM.
        }
    }

    private void backgroundProcesses() {
        // TODO
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    @Override
    public AtomManifest addAtom(Collection<LocationBundle> locations)
            throws ManifestNotMadeException, ManifestSaveException, DataStorageException {

        AtomManifest manifest = ManifestFactory.createAtomManifest(configuration, locations);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AtomManifest addAtom(InputStream inputStream) {
        return null;
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
        Collection<LocationBundle> locations = atomManifest.getLocations();
        for(LocationBundle location:locations) {

            try {
                dataStream = DataStorage.getInputStreamFromLocation(location.getLocation());
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
    public Identity getIdentity() {
        return this.identity;
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException {
        boolean ret;
        try {
            ret = manifest.verify(identity);
        } catch (GuidGenerationException | DecryptionException e) {
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
    public Collection<GUID> findVersions(GUID invariant) {
        return manifestsManager.findVersions(invariant);
    }
}
