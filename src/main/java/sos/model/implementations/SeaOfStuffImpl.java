package sos.model.implementations;

import IO.ManifestStream;
import sos.configurations.DefaultConfiguration;
import sos.configurations.SeaConfiguration;
import sos.exceptions.*;
import sos.managers.ManifestsManager;
import sos.managers.MemCache;
import sos.managers.RedisCache;
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestFactory;
import sos.model.implementations.identity.Session;
import sos.model.implementations.policies.DefaultPolicy;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.components.Manifest;
import sos.model.interfaces.components.Metadata;
import sos.model.interfaces.identity.Identity;
import sos.model.interfaces.identity.IdentityToken;
import sos.model.interfaces.policies.Policy;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate components
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffImpl implements SeaOfStuff {

    private Session session;
    private Policy policy;
    private ManifestsManager manifestsManager;
    private SeaConfiguration configuration;
    private MemCache cache;

    public SeaOfStuffImpl() {
        configuration = new DefaultConfiguration(); // TODO - load configuration from file.

        session = new Session();
        policy = new DefaultPolicy();
        cache = RedisCache.getInstance();
        manifestsManager = new ManifestsManager(configuration, policy, cache);

        // TODO - start background processes
        // listen to incoming requests from other nodes / crawlers?
        // make this node available to the rest of the sea of stuff
    }

    public void cleanup() {
        // XXX - temporary solution. This should be done form the cache object.
        RedisCache.getInstance().killInstance();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public IdentityToken registerIdentity(Identity identity) {
        return session.addIdentity(identity);
    }

    @Override
    public void unregisterIdentity(IdentityToken identityToken)
            throws UnknownIdentityException {

        session.removeIdentity(identityToken);
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

        Identity identity = session.getRegisteredIdentity();
        CompoundManifest manifest = ManifestFactory.createCompoundManifest(contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AssetManifest addAsset(Content content,
                                  Collection<GUID> prevs,
                                  GUID metadata)
            throws ManifestNotMadeException, ManifestSaveException {

        Identity identity = session.getRegisteredIdentity();
        AssetManifest manifest = ManifestFactory.createAssetManifest(content, prevs, metadata, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public byte[] getAtomContent(AtomManifest atomManifest) {

        // TODO - get locations from atomManifest and retrieve atom.
        // TODO - query the manifests manager
        // abstract implementation from this class

        throw new NotImplementedException();
    }

    @Override
    public Manifest getManifest(GUID guid) throws UnknownGUIDException {
        Manifest manifest = null;
        try {
            manifest = manifestsManager.findManifest(guid);
        } catch (UnknownManifestTypeException e) {
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
    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    @Override
    public void unsetPolicy(Policy policy) {
        this.policy = new DefaultPolicy();
    }

    @Override
    public ManifestStream findManifests(Metadata metadata) {
        // TODO - look at manifests manager
        // having a look at the redis cache would be very helpful!
        throw new NotImplementedException();
    }
}
