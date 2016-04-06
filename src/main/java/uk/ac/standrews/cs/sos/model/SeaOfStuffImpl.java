package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyGenerationException;
import uk.ac.standrews.cs.sos.exceptions.identity.KeyLoadedException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.utils.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.identity.IdentityImpl;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.ProvenanceLocationBundle;
import uk.ac.standrews.cs.sos.model.locations.sos.url.SOSURLStreamHandlerFactory;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.storage.DataStorageHelper;
import uk.ac.standrews.cs.utils.GUIDFactory;
import uk.ac.standrews.cs.utils.IGUID;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffImpl implements SeaOfStuff {

    private Identity identity;
    private ManifestsManager manifestsManager;
    final private SeaConfiguration configuration;

    public SeaOfStuffImpl(SeaConfiguration configuration, Index index) throws SeaOfStuffException {
        this.configuration = configuration;

        try {
            generateSOSNodeIfNone();
            identity = new IdentityImpl(configuration);
            manifestsManager = new ManifestsManager(configuration, index);
        } catch (GUIDGenerationException | SeaConfigurationException |
                KeyGenerationException | KeyLoadedException e) {
            throw new SeaOfStuffException();
        }

        backgroundProcesses();
        registerSOSProtocol();
    }

    private void generateSOSNodeIfNone() throws GUIDGenerationException, SeaConfigurationException {
        IGUID nodeId = configuration.getNodeId();
        if (nodeId == null) {
            nodeId = GUIDFactory.generateRandomGUID();
            configuration.setNodeId(nodeId);
        }
    }

    private void registerSOSProtocol() {
        try {
            URL.setURLStreamHandlerFactory(new SOSURLStreamHandlerFactory());
        } catch (Error e) {
            System.err.println("SeaOfStuffImpl::registerSOSProtocol:" + e.getMessage());
        }
    }

    private void backgroundProcesses() {
        // - start background processes
        // - listen to incoming requests from other nodes / crawlers?
        // - make this node available to the rest of the sea of stuff
    }

    @Override
    public Atom addAtom(Location location)
            throws ManifestPersistException, DataStorageException {

        Collection<LocationBundle> bundles = new ArrayList<>();
        bundles.add(new ProvenanceLocationBundle(location));

        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, location, bundles);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, bundles);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Atom addAtom(InputStream inputStream)
            throws ManifestPersistException, DataStorageException {

        Collection<LocationBundle> locations = new ArrayList<>();
        IGUID guid = DataStorageHelper.cacheAtomAndUpdateLocationBundles(configuration, inputStream, locations);
        AtomManifest manifest = ManifestFactory.createAtomManifest(guid, locations);
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
    public Asset addAsset(IGUID content,
                          IGUID invariant,
                          Collection<IGUID> prevs,
                          Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException {

        AssetManifest manifest = ManifestFactory.createAssetManifest(content, invariant, prevs, metadata, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public Manifest addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsManager.addManifest(manifest);

        // TODO - recursively look for other manifests to add to the SOS

        return manifest;
    }

    @Override
    public InputStream getAtomContent(Atom atom) {
        InputStream dataStream = null;
        Collection<LocationBundle> locations = atom.getLocations();
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
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
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
        } catch (GUIDGenerationException | DecryptionException e) {
            throw new ManifestVerificationFailedException();
        }

        return ret;
    }

    @Override
    public Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException {
        return manifestsManager.findManifestsByType(type);
    }

    @Override
    public Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException {
        return manifestsManager.findManifestsThatMatchLabel(label);
    }

    @Override
    public Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException {
        return manifestsManager.findVersions(invariant);
    }
}
