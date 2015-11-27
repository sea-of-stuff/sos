package model.implementations;

import model.exceptions.UnknownGUIDException;
import model.exceptions.UnknownIdentityException;
import model.implementations.components.identity.Session;
import model.implementations.components.manifests.AssetManifest;
import model.implementations.components.manifests.AtomManifest;
import model.implementations.components.manifests.CompoundManifest;
import model.implementations.components.manifests.ManifestFactory;
import model.implementations.utils.GUID;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.entities.Manifest;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;
import model.services.ServiceManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate components
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffImpl implements SeaOfStuff {

    private Session session;
    private ServiceManager serviceManager;

    public SeaOfStuffImpl() {
        session = new Session();
    }

    @Override
    public IdentityToken register(Identity identity) {
        return session.addIdentity(identity);
    }

    @Override
    public void unregister(IdentityToken identityToken) throws UnknownIdentityException {
        session.removeIdentity(identityToken);
    }

    public Session getSession() {
        return session;
    }

    @Override
    public AtomManifest addAtom(Atom atom) {
        Identity identity = session.getRegisteredIdentity();
        AtomManifest manifest = ManifestFactory.createAtomManifest(atom, identity);

        // TODO - add atom and manifest to Sea of Stuff

        return manifest;
    }

    @Override
    public Atom getAtomContent(AtomManifest atomManifest) {

        // TODO - get locations from atomManifest and retrieve atom.

        throw new NotImplementedException();
    }

    @Override
    public void addCompound(CompoundManifest compoundManifest) {
        throw new NotImplementedException();
    }

    @Override
    public void addAsset(AssetManifest assetManifest) {
        throw new NotImplementedException();
    }

    @Override
    public Manifest getManifest(GUID guid) throws UnknownGUIDException {

        // TODO - lookup localdb or webservice for GUID

        throw new NotImplementedException();
    }

    @Override
    public boolean verifyManifest(Manifest manifest) {

        // TODO - verify manifest through GUIDs and hashing

        throw new NotImplementedException();
    }

    @Override
    public void setPolicy() {

    }
}
