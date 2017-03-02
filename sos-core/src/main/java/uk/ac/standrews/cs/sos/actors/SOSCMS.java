package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.CMS;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.context.ContextDirectory;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.model.context.ContextDirectoryImpl;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private ContextDirectory contextDirectory;
    private DDS dds;

    public SOSCMS(DDS dds, Identity identity) {
        this.dds = dds;
        contextDirectory = new ContextDirectoryImpl(identity);
    }

    @Override
    public Asset addContext(Context context) throws Exception {

        try {
            Asset asset = contextDirectory.add(context);
            dds.addManifest(asset, false);
            return asset;
        } catch (ManifestPersistException e) {
            throw new ContextException(e);
        }
    }
}
