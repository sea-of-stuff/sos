package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

/**
 * The context index defines the relationship between contexts and assets
 *
 * TODO - how can this relationship be shared? maybe we could store some little manifests recording this information?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextIndex {

    /**
     * Add a given asset under the specified context
     * @param context
     * @param asset
     */
    void addToContext(Context context, Asset asset);

    /**
     * Get all assets associated with the given context
     * @param context
     */
    void getAssets(Context context);
}
