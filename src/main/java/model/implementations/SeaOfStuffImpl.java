package model.implementations;

import model.exceptions.UnknownGUIDException;
import model.exceptions.UnknownIdentityException;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;
import model.interfaces.components.manifests.AssetManifest;
import model.interfaces.components.manifests.AtomManifest;
import model.interfaces.components.manifests.CompoundManifest;
import model.interfaces.components.manifests.Manifest;
import model.interfaces.components.utils.GUID;
import model.interfaces.entities.Atom;
import model.interfaces.entities.Compound;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffImpl implements SeaOfStuff {
    @Override
    public IdentityToken register(Identity identity) {
        return null;
    }

    @Override
    public void unregister(IdentityToken identityToken) throws UnknownIdentityException {

    }

    @Override
    public Manifest addAtom(Atom atom) {
        return null;
    }

    @Override
    public Manifest addCompound(Compound compound) {
        return null;
    }

    @Override
    public void addAsset(AssetManifest assetManifest) {

    }

    @Override
    public Manifest getManifest(GUID guid) throws UnknownGUIDException {
        return null;
    }

    @Override
    public Atom getAtomContent(AtomManifest atomManifest) {
        return null;
    }

    @Override
    public Compound getCompoundContent(CompoundManifest compoundManifest) {
        return null;
    }

    @Override
    public boolean verifyManifest(Manifest manifest) {
        return false;
    }
}
