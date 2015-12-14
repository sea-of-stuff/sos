package sos.model.implementations;

import IO.ManifestStream;
import sos.configurations.DefaultConfiguration;
import sos.configurations.SeaConfiguration;
import sos.exceptions.*;
import sos.managers.ManifestsManager;
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

    public SeaOfStuffImpl() {
        configuration = new DefaultConfiguration(); // TODO - load configuration from file.

        session = new Session();
        policy = new DefaultPolicy();
        manifestsManager = new ManifestsManager(configuration, policy);

        // TODO - start background processes
        // listen to incoming requests from other nodes / crawlers?
        // make this node available to the rest of the sea of stuff
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
            throws ManifestNotMadeException {

        AtomManifest manifest = ManifestFactory.createAtomManifest(locations);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public CompoundManifest addCompound(Collection<Content> contents)
            throws ManifestNotMadeException {

        Identity identity = session.getRegisteredIdentity();
        CompoundManifest manifest = ManifestFactory.createCompoundManifest(contents, identity);
        manifestsManager.addManifest(manifest);

        return manifest;
    }

    @Override
    public AssetManifest addAsset(Content content,
                                  Collection<GUID> prevs,
                                  GUID metadata)
            throws ManifestNotMadeException {

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

        // TODO - lookup localdb or webservice for GUID
        // manifests manager.
        // abstract implementation

        throw new NotImplementedException();
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
        throw new NotImplementedException();
    }
}
