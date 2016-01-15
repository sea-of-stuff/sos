package sos.model.implementations;

import IO.ManifestStream;
import sos.configurations.SeaConfiguration;
import sos.exceptions.*;
import sos.managers.ManifestsManager;
import sos.managers.MemCache;
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestFactory;
import sos.model.implementations.identity.IdentityImpl;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.components.Metadata;
import sos.model.interfaces.identity.Identity;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
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
    private MemCache cache;

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
            throws ManifestNotMadeException, ManifestSaveException {

        AtomManifest manifest = ManifestFactory.createAtomManifest(locations);
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
    public byte[] getAtomContent(AtomManifest atomManifest) {
        // - get locations from atomManifest and retrieve atom.
        // - query the manifests manager
        // - abstract implementation from this class
        throw new NotImplementedException();
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
    public ManifestStream findManifests(Metadata metadata) {
        // - look at manifests manager
        // - having a look at the redis cache would be very helpful!
        throw new NotImplementedException();
    }
}
