package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.Signature;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

/**
 * A compound is an immutable collection of (references to)
 * atoms or other compounds (contents).
 * FIXME - Compounds do not contain data
 * - they refer to data - and are identified by GUID (derived from their contents).
 *
 * <p>
 * Intuition: <br>
 * Compounds are provided to permit related atoms and compounds to be gathered
 * together (think of folders, zip files, packages etc. without containment).
 * <p>
 * A compound can be used for de-duplication. Two collections of data
 * (atoms and compounds) might contain the same content. The data does not have
 * to be duplicated for each compound, since we can uniquely refer to the data
 * from the compound itself.
 *
 *
 * Manifest describing a Compound.
 *
 * <p>
 * Manifest - GUID <br>
 * ManifestType - COMPOUND <br>
 * Timestamp - ? <br>
 * Signature - signature of the manifest <br>
 * Locations - list of GUIDs/locations <br>
 * Content - GUID Content
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

public class CompoundManifest extends BasicManifest {

    protected CompoundManifest() {
        super(ManifestConstants.COMPOUND);
    }

    public Collection<GUID> getContents() {
        throw new NotImplementedException();
    }

    @Override
    public boolean verify() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    protected GUID generateGUID() {
        return null;
    }

    @Override
    protected Signature generateSignature(Identity identity) {
        return null;
    }

}
