package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.implementations.utils.GUIDsha1;
import model.implementations.utils.Location;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.Signature;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * Manifest describing an Atom.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - ATOM <br>
 * Timestamp - ? <br>
 * Signature - signature of the manifest <br>
 * Locations - list of locations <br>
 * Content - GUID Content
 * </p>
 *
 * @see Atom
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifest extends BasicManifest {

    private GUID contentGUID;
    private Collection<Location> locations;

    /**
     * Creates a partially valid atom manifest given an atom.
     *
     * @param atom
     */
    protected AtomManifest(Atom atom) {
        super(ManifestConstants.ATOM);
        contentGUID = new GUIDsha1(atom.getSource().getInputStream());

        // TODO - how about locations?
    }

    public GUID getGUIDContent() {
        return contentGUID;
    }

    public Collection<Location> getLocations() {
        return locations;
    }

    public boolean verify() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isValid() {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        throw new NotImplementedException();
    }

    @Override
    protected GUID generateGUID() {
        return null;
    }

    @Override
    protected Signature generateSignature(Identity identity) {
        throw new NotImplementedException();
    }

}
