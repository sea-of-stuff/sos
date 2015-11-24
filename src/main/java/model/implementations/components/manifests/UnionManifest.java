package model.implementations.components.manifests;

import model.implementations.utils.GUID;
import model.implementations.utils.Location;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class UnionManifest extends ManifestImpl {

    protected UnionManifest(String manifestType) {
        super(manifestType);
    }

    /**
     * GUID of the content this manifest refers to.
     *
     * @return GUID of the content
     */
    public abstract GUID getGUIDContent();

    /**
     * A collection of locations is returned. These are the locations where
     * the content that this manifest refers to are.
     * @return
     */
    public abstract Collection<Location> getLocations();
}
