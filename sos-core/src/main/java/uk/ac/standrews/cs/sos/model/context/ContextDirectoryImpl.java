package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.interfaces.context.ContextDirectory;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;

import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO - add persistency
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextDirectoryImpl implements ContextDirectory {

    private HashMap<IGUID, Context> contexts;

    private Identity identity; // FIXME - this should not be fixed, but can change voer time. see SOSAgent too

    public ContextDirectoryImpl(Identity identity) {
        contexts = new HashMap<>();

        this.identity = identity;
    }

    @Override
    public Asset add(Context context) throws ContextException {
        contexts.put(context.getGUID(), context);


        try {
            Asset asset = ManifestFactory.createVersionManifest(context.getGUID(), null, null, null, identity);
            return asset;

        } catch (ManifestNotMadeException e) {
            throw new ContextException(e);
        }
    }

    @Override
    public Context get(IGUID version) {
        return null;
    }

    @Override
    public Asset update(IGUID guid, Context context) {
        return null;
    }

    @Override
    public Asset remove(IGUID guid) {
        return null;
    }

    @Override
    public void add(IGUID contextGUID, Asset asset) {



    }

    @Override
    public Iterator<IGUID> getContents(IGUID contextGUID) {
        return null;
    }
}
