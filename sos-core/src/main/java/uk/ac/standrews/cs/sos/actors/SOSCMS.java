package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.CMS;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Version;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSCMS implements CMS {

    private DDS dds;

    public SOSCMS(DDS dds) {
        this.dds = dds;
    }

    @Override
    public Version addContext(Context context) throws Exception {

        try {
            Version version = ManifestFactory.createVersionManifest(context.guid(), null, null, null, null);

            dds.addManifest(context, false);
            dds.addManifest(version, false);

            return version;
        } catch (ManifestPersistException e) {
            throw new ContextException(e);
        }
    }

    @Override
    public Context getContext(IGUID version) {

        try {
            Manifest manifest = dds.getManifest(version);
            return (Context) dds.getManifest(((Version) manifest).getContentGUID());
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Version update(IGUID version, Context context) {


        // Create new version with version as previous
//        try {
//            Asset asset = ManifestFactory.createVersionManifest(context.guid(), null, null, null, null);
//
//            dds.addManifest(context, false);
//            dds.addManifest(asset, false);
//
//            return asset;
//        } catch (ManifestPersistException e) {
//            throw new ContextException(e);
//        }

        return null;
    }

    @Override
    public Version remove(IGUID guid) {
        // Create new version without context

        return null;
    }

    @Override
    public Iterator<IGUID> getContents(IGUID version) {
        return null;
    }

    @Override
    public boolean verify(IGUID context, IGUID version) {
        return false;
    }

    @Override
    public void getActiveContexts() {

    }

    @Override
    public void setContext(Context context, boolean active) {

    }

    // TODO -  schedule contexts? have priority over what contexts to run first?
    @Override
    public void process() {

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleWithFixedDelay(() -> {
            // execute contexts on assets

        }, 0, 1, TimeUnit.MINUTES);
    }
}
