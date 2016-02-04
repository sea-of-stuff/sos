package uk.ac.standrews.cs.sos.managers;

import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonIndex extends Index {

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
            // TODO - might occur in other cases too (e.g. index could not be created)
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
