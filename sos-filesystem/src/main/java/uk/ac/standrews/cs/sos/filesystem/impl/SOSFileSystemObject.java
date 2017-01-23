package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class SOSFileSystemObject extends FileSystemObject implements IVersionableObject, SOSVersionableObject {

    protected Agent sos;

    protected Asset asset;
    protected SOSDirectory parent;
    protected SOSFileSystemObject previous; // TODO - make collection (e.g. merging)
    protected SOSMetadata metadata;

    public SOSFileSystemObject(Agent sos) {
        super(null);
        this.sos = sos;
    }

    public SOSFileSystemObject(Agent sos, IData data) {
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
                asset = sos.addVersion(builder);

                if (assetIsWebdavRoot(asset)) {
                    SOSFileSystemFactory.WriteCurrentVersion(asset.getInvariantGUID(), asset.getVersionGUID());
                }

                guid = asset.getVersionGUID();
            } else {
                // This happens if data is the same? test by having multiple of the same commands
                // e.g. echo data > test.txt; echo data > test.txt; echo data > test.txt
                System.out.println("WOOOOOO - some strange issue here");
            }
        } catch (ManifestNotMadeException | ManifestPersistException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean assetIsWebdavRoot(Asset asset) {
        File file = new File(SOSFileSystemFactory.WEBDAV_CURRENT_PATH + asset.getInvariantGUID());
        return file.exists();
    }


    @Override
    public Set<IGUID> getPrevious() {
        return asset.getPreviousVersions();
    }

    @Override
    public IGUID getInvariant() {
        return asset.getInvariantGUID();
    }

    public Asset getAsset() {
        return asset;
    }

    // MUST BE IMPLEMENTED by subclasses
    protected IGUID getContentGUID() { return null; }

    protected boolean checkPreviousDiffers(IGUID contentGUID) {
        if (previous != null) {
            IGUID previousContentGUID = previous.getAsset().getContentGUID();
            return !previousContentGUID.equals(contentGUID);
        }

        return true;
    }

    // this is a bad way of dealing with previous references.
    // It should be possible to deal with multiple previous. Not sure how this works with webdav integration however
    private VersionBuilder getVersionBuilder(IGUID contentGUID) throws ManifestPersistException, ManifestNotMadeException {

        Set<IGUID> prevs = new LinkedHashSet<>();
        if (previous != null) {
            IGUID versionGUID = previous.getAsset().getVersionGUID();
            prevs.add(versionGUID);
        } else if (asset != null && asset.getPreviousVersions() != null) {
            prevs.addAll(asset.getPreviousVersions());
        }

        VersionBuilder builder = new VersionBuilder(contentGUID);

        if (asset != null) {
            builder.setInvariant(asset.getInvariantGUID());
        }

        if (prevs.size() > 0) {
            builder.setPrevious(prevs);
        }

        if (metadata != null) {
            builder.setMetadata(metadata);
        }

        return builder;
    }
}
