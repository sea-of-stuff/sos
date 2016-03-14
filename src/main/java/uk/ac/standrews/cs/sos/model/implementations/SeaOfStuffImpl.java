package uk.ac.standrews.cs.sos.model.implementations;

import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.exceptions.*;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.managers.Index;
import uk.ac.standrews.cs.sos.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.*;
import uk.ac.standrews.cs.sos.model.implementations.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.implementations.locations.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.implementations.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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

    public SeaOfStuffImpl(SeaConfiguration configuration, Index index) throws KeyGenerationException, KeyLoadedException, IOException { // TODO - have only one exception
        this.configuration = configuration;

        try {
            generateSOSNodeIfNone();
        } catch (GuidGenerationException | SeaConfigurationException e) {
            e.printStackTrace(); // FIXME - throw exception
        }

        identity = new IdentityImpl(configuration);
        manifestsManager = new ManifestsManager(configuration, index);

        backgroundProcesses();
        registerSOSProtocol();
    }

    private void generateSOSNodeIfNone() throws GuidGenerationException, SeaConfigurationException {
        GUID nodeId = configuration.getNodeId();
        if (nodeId == null) {
            nodeId = GUID.generateRandomGUID();
            configuration.setNodeId(nodeId);
        }
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
            throws ManifestNotMadeException, ManifestPersistException, DataStorageException {

        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, locations);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, locations);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AtomManifest addAtom(InputStream inputStream)
            throws ManifestNotMadeException, ManifestPersistException, DataStorageException {

        // Cache the data
        // generate manifest
        Collection<LocationBundle> locations = new ArrayList<LocationBundle>();
        GUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, locations);
        manifestsManager.addManifest(manifest);

        return null;
    }

    @Override
    public CompoundManifest addCompound(Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AssetManifest addAsset(Content content,
                                  GUID invariant,
                                  Collection<GUID> prevs,
                                  Collection<GUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException {

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
                dataStream = DataStorageHelper.getInputStreamFromLocation(location.getLocation());
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
    public Manifest getManifest(GUID guid) throws ManifestNotFoundException {
        Manifest manifest;
        try {
            manifest = manifestsManager.findManifest(guid);
        } catch (ManifestNotFoundException e) {
            throw new ManifestNotFoundException();
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
    public Collection<GUID> findManifestByType(String type) throws ManifestNotFoundException {
        return manifestsManager.findManifestsByType(type);
    }

    @Override
    public Collection<GUID> findManifestByLabel(String label) throws ManifestNotFoundException {
        return manifestsManager.findManifestsThatMatchLabel(label);
    }

    @Override
    public Collection<GUID> findVersions(GUID invariant) throws ManifestNotFoundException {
        return manifestsManager.findVersions(invariant);
    }
}
