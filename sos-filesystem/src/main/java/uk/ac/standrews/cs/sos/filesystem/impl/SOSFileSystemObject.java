package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.persistence.impl.AttributedStatefulObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemObject extends AttributedStatefulObject implements IVersionableObject, SOSVersionableObject {

    protected Client sos;

    protected String name;
    protected Version version;
    protected SOSDirectory parent;
    protected SOSFileSystemObject previous;

    public SOSFileSystemObject(Client sos) {
        super(null);
        this.sos = sos;
    }

    public SOSFileSystemObject(Client sos, IData data) {
        super(data, null);
        this.sos = sos;
    }

    @Override
    public long getCreationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public void persist() throws PersistenceException {
        try {
            IGUID contentGUID = getContentGUID();
            boolean previousVersionDiffers = checkPreviousDiffers(contentGUID);

            if (previousVersionDiffers) {
                VersionBuilder builder = getVersionBuilder(contentGUID);
                version = sos.addVersion(builder); // TODO - not sure if this is needed, since we create it in constructor too

                sos.setHEAD(version.getVersionGUID());
                guid = version.getVersionGUID();
            } else {
                System.out.println("WOOOOOO");
            }
        } catch (ManifestNotMadeException | ManifestPersistException | HEADNotSetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<IGUID> getPrevious() {
        return version.getPreviousVersions();
    }

    @Override
    public IGUID getInvariant() {
        return version.getInvariantGUID();
    }

    @Override
    public Version getVersion() {
        return version;
    }

    // MUST BE IMPLEMENTED
    protected IGUID getContentGUID() { return null; }

    protected boolean checkPreviousDiffers(IGUID contentGUID) {
        if (previous != null) {
            return !previous.getVersion().getContentGUID().equals(contentGUID);
        }

        return true;
    }

    protected VersionBuilder getVersionBuilder(IGUID contentGUID) throws ManifestPersistException, ManifestNotMadeException {
        // FIXME - this is a bad way of dealing with previous references.
        // It should be possible to deal with multiple previous. Not sure how this works with webdav integration however
        Collection<IGUID> prevs = new ArrayList<>();
        if (previous != null) {
            IGUID versionGUID = previous.getVersion().getVersionGUID();
            prevs.add(versionGUID);
        }

        VersionBuilder builder = new VersionBuilder(contentGUID);

        if (version != null) {
            builder.setInvariant(version.getInvariantGUID());
        }

        if (prevs.size() > 0) {
            builder.setPrevious(prevs);

            SOS_LOG.log(LEVEL.INFO, "WEBDAT - SOSFile - Previous: " + previous.toString());
            SOS_LOG.log(LEVEL.INFO, "WEBDAV - SOSFile - Set prev for asset with invariant " + previous.getInvariant());
        }

        // TODO - add metadata

        return builder;
    }
}
