package factories;

import interfaces.components.Metadata;
import interfaces.entities.Asset;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AssetFactory {

    /**
     * TODO
     * @param union
     * @return
     */
    Asset makeAsset(Union union);

    /**
     * TODO
     * @param union
     * @param metadata
     * @return
     */
    Asset makeAsset(Union union, Metadata metadata);

    /**
     * TODO
     * @param union
     * @param previousAsset
     * @return
     */
    Asset makeAsset(Union union, Asset previousAsset);

    /**
     * TODO
     * @param union
     * @param previousAsset
     * @param metadata
     * @return
     */
    Asset makeAsset(Union union, Asset previousAsset, Metadata metadata);
}
