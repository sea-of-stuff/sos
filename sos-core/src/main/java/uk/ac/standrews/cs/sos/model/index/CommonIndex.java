package uk.ac.standrews.cs.sos.model.index;

import uk.ac.standrews.cs.sos.exceptions.index.IndexException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonIndex implements Index {

    @Override
    public void addManifest(Manifest manifest) throws IndexException {
        String type = manifest.getManifestType();
        switch (type) {
            case ManifestConstants.ATOM:
                addAtomManifest((AtomManifest) manifest);
                break;
            case ManifestConstants.COMPOUND:
                addCompoundManifest((CompoundManifest) manifest);
                break;
            case ManifestConstants.VERSION:
                addAssetManifest((VersionManifest) manifest);
                break;
            default:
                throw new IndexException("Manifest type: " + type + " is unknown");
        }
    }

    protected abstract void addAtomManifest(AtomManifest manifest) throws IndexException;

    protected abstract void addCompoundManifest(CompoundManifest manifest) throws IndexException;

    protected abstract void addAssetManifest(VersionManifest manifest) throws IndexException;

}
