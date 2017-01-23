package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

import java.util.Iterator;

/**
 * The context index defines the relationship between contexts and assets
 *
 * TODO - how can this relationship be shared? maybe we could store some little manifests recording this information?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextDirectory {

    /**
     * Add this context to the directory
     * @param context
     */
    void addContext(Context context);

    /**
     * Get context from this directory given its GUID
     * @param contextGUID
     * @return context matching the contextGUID
     */
    Context getContext(IGUID contextGUID);

    /**
     * Add a given asset under the specified context
     * @param contextGUID
     * @param asset
     */
    void addToContext(IGUID contextGUID, Asset asset);

    /**
     * Get all assets associated with the given context
     * @param contextGUID
     * @return Iterator<IGUID> iterator of GUIDs referencing to assets in the SOS
     */
    Iterator<IGUID> getFromContext(IGUID contextGUID);
}
