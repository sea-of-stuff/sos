package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.implementations.utils.GUIDsha1;
import model.implementations.utils.Location;
import model.interfaces.components.entities.Atom;

import java.util.Collection;

/**
 * Atom Manifest implementation
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends Manifest implements model.interfaces.components.manifests.AtomManifest  {

    private GUID contentGUID;

    public AtomManifest(Atom atom) {
        contentGUID = new GUIDsha1(atom.getSource());

        // TODO - how about locations?
    }

    public GUID getGUIDContent() {
        return contentGUID;
    }

    public Collection<Location> getLocations() {
        return null;
    }

    public boolean verify() {
        return false;
    }
}
