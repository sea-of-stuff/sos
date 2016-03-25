package uk.ac.standrews.cs.sos.model.index;

import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.interfaces.Index;
import uk.ac.standrews.cs.sos.interfaces.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.utils.GUID;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonIndex implements Index {

    @Override
    public void addManifest(Manifest manifest) throws UnknownManifestTypeException {
        String type = manifest.getManifestType();
        try {
            switch(type) {
                case ManifestConstants.ATOM:
                    addAtomManifest((AtomManifest) manifest);
                    break;
                case ManifestConstants.COMPOUND:
                    addCompoundManifest((CompoundManifest) manifest);
                    break;
                case ManifestConstants.ASSET:
                    addAssetManifest((AssetManifest) manifest);
                    break;
                default:
                    throw new UnknownManifestTypeException();
            }
        } catch (IOException e) {
            throw new UnknownManifestTypeException();
        }
    }

    protected abstract void addAtomManifest(AtomManifest manifest) throws IOException;

    protected abstract void addCompoundManifest(CompoundManifest manifest) throws IOException;

    protected abstract void addAssetManifest(AssetManifest manifest) throws IOException;

    @Override
    public Collection<Content> getContents(GUID contentGUID) {
        // TODO - read from file
        return null;
    }

}
