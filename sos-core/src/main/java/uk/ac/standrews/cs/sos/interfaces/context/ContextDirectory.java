package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

import java.util.Iterator;

/**
 * The context index defines the relationship between contexts and assets
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ContextDirectory {

    /**
     * Add this context to the directory
     * @param context
     */
    Asset add(Context context) throws ContextException;

    /**
     * Get context from this directory given its GUID
     * @param version
     * @return context matching the version GUID
     */
    Context get(IGUID version);

    /**
     * Update the context with given GUID with new version
     * @param guid
     * @param context
     * @return asset version pointing to new context and referring to the previous context
     */
    Asset update(IGUID guid, Context context);

    /**
     *
     * @param guid
     * @return
     */
    Asset remove(IGUID guid);

    /**
     * Add a given asset under the specified context
     * @param contextGUID
     * @param asset
     */
    void add(IGUID contextGUID, Asset asset);

    /**
     * Get all assets associated with the given context
     * @param contextGUID
     * @return Iterator<IGUID> iterator of GUIDs referencing to assets in the SOS
     */
    Iterator<IGUID> getContents(IGUID contextGUID);


}
