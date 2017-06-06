package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.sos.actors.Agent;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;
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

    protected Version version;
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
                VersionBuilder builder = getVersionBuilder(contentGUID);
                version = sos.addVersion(builder);

                if (assetIsWebDAVRoot(version)) {
                    SOSFileSystemFactory.WriteCurrentVersion(version.getInvariantGUID(), version.getVersionGUID());
                }

                guid = version.getVersionGUID();
            } else {
                SOS_LOG.log(LEVEL.WARN, "Version has exactly the same data. Metadata, however, might have changed.");
            }
        } catch (ManifestNotMadeException | ManifestPersistException | FileNotFoundException | RoleNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean assetIsWebDAVRoot(Version version) {
        File file = new File(SOSFileSystemFactory.WEBDAV_CURRENT_PATH + version.getInvariantGUID());
        return file.exists();
    }

    @Override
    public Set<IGUID> getPrevious() {
        return version.getPreviousVersions();
    }

    @Override
    public IGUID getInvariant() {
        return version.getInvariantGUID();
    }

    @Override
    public void setParent(IDirectory parent) {
        this.parent = (SOSDirectory) parent;
    }

    public IDirectory getParent() {
        return parent;
    }

    public Version getVersion() {
        return version;
    }

    // MUST BE IMPLEMENTED by subclasses
    protected IGUID getContentGUID() {
        return null;
    }

    protected boolean previousAssetDiffers(IGUID contentGUID) {
        if (previous != null) {
            IGUID previousContentGUID = previous.getVersion().getContentGUID();
            return !previousContentGUID.equals(contentGUID);
        }

        return true;
    }

    protected SOSFileSystemObject getPreviousObject() {
        return previous;
    }

    // Get a asset builder for an asset with the specified content GUID
    private VersionBuilder getVersionBuilder(IGUID contentGUID) throws ManifestPersistException, ManifestNotMadeException {

        VersionBuilder builder = new VersionBuilder(contentGUID);

        if (version != null) {
            builder.setInvariant(version.getInvariantGUID());
        }

        Set<IGUID> prevs = new LinkedHashSet<>();
        if (previous != null) {
            IGUID versionGUID = previous.getVersion().getVersionGUID();
            prevs.add(versionGUID);
        } else if (version != null && version.getPreviousVersions() != null) {
            prevs.addAll(version.getPreviousVersions());
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
