package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.model.manifests.builders.AssetBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class SOSFileSystemObject extends FileSystemObject implements IVersionableObject {

    protected Agent sos;

    protected Asset asset;
    protected SOSDirectory parent;
    protected SOSFileSystemObject previous; // TODO - make collection (e.g. for merging)
    protected Metadata metadata;

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
            boolean previousVersionDiffers = previousAssetDiffers(contentGUID);

            if (previousVersionDiffers) {
                AssetBuilder builder = getAssetBuilder(contentGUID);
                asset = sos.addAsset(builder);

                if (assetIsWebDAVRoot(asset)) {
                    SOSFileSystemFactory.WriteCurrentVersion(asset.getInvariantGUID(), asset.getVersionGUID());
                }

                guid = asset.getVersionGUID();
            } else {
                SOS_LOG.log(LEVEL.WARN, "Asset has exactly the same data. Metadata, however, might have changed.");
            }
        } catch (ManifestNotMadeException | ManifestPersistException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean assetIsWebDAVRoot(Asset asset) {
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

    @Override
    public void setParent(IDirectory parent) {
        this.parent = (SOSDirectory) parent;
    }

    public IDirectory getParent() {
        return parent;
    }

    public Asset getAsset() {
        return asset;
    }

    // MUST BE IMPLEMENTED by subclasses
    protected IGUID getContentGUID() {
        return null;
    }

    protected boolean previousAssetDiffers(IGUID contentGUID) {
        if (previous != null) {
            IGUID previousContentGUID = previous.getAsset().getContentGUID();
            return !previousContentGUID.equals(contentGUID);
        }

        return true;
    }

    protected SOSFileSystemObject getPreviousObject() {
        return previous;
    }

    // Get a asset builder for an asset with the specified content GUID
    private AssetBuilder getAssetBuilder(IGUID contentGUID) throws ManifestPersistException, ManifestNotMadeException {

        AssetBuilder builder = new AssetBuilder(contentGUID);

        if (asset != null) {
            builder.setInvariant(asset.getInvariantGUID());
        }

        Set<IGUID> prevs = new LinkedHashSet<>();
        if (previous != null) {
            IGUID versionGUID = previous.getAsset().getVersionGUID();
            prevs.add(versionGUID);
        } else if (asset != null && asset.getPreviousVersions() != null) {
            prevs.addAll(asset.getPreviousVersions());
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
