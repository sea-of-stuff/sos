/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module filesystem.
 *
 * filesystem is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * filesystem is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with filesystem. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.persistence.impl.FileSystemObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.persistence.interfaces.IVersionableObject;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemFactory;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.Agent;
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
                    SOSFileSystemFactory.WriteCurrentVersion(version.invariant(), version.version());
                }

                guid = version.version();
            } else {
                SOS_LOG.log(LEVEL.WARN, "Version has exactly the same data. Metadata, however, might have changed.");
            }
        } catch (ServiceException | ManifestNotMadeException | ManifestPersistException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean assetIsWebDAVRoot(Version version) {
        File file = new File(SOSFileSystemFactory.WEBDAV_CURRENT_PATH + version.invariant());
        return file.exists();
    }

    @Override
    public Set<IGUID> getPrevious() {
        return version.previous();
    }

    @Override
    public IGUID getInvariant() {
        return version.invariant();
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
            IGUID previousContentGUID = previous.getVersion().content();
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
            builder.setInvariant(version.invariant());
        }

        Set<IGUID> prevs = new LinkedHashSet<>();
        if (previous != null) {
            IGUID versionGUID = previous.getVersion().version();
            prevs.add(versionGUID);
        } else if (version != null && version.previous() != null) {
            prevs.addAll(version.previous());
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
