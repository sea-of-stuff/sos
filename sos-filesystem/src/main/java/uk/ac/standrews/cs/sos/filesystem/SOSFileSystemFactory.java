package uk.ac.standrews.cs.sos.filesystem;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.impl.SOSFileSystem;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.sos.Agent;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    private Agent agent;
    private IGUID rootGUID;

    public SOSFileSystemFactory(Agent agent, IGUID rootGUID) {
        this.agent = agent;
        this.rootGUID = rootGUID;
    }

    public SOSFileSystemFactory(IGUID rootGUID) {
        this.rootGUID = rootGUID;
    }

    @Override
    public IFileSystem makeFileSystem() throws FileSystemCreationException {
        if (agent != null && rootGUID != null) {
            return makeFileSystem(agent);
        }

        throw new FileSystemCreationException();
    }

    public IFileSystem makeFileSystem(Agent agent) throws FileSystemCreationException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Factory - Making the File System");

        if (agent != null) {
            Asset rootAsset = createRoot(agent);
            return new SOSFileSystem(agent, rootAsset);
        } else {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Unable to create file system");
            return null;
        }
    }

    private Asset createRoot(Agent sos) {
        Asset retval = getRoot(sos, rootGUID);

        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating ROOT " + rootGUID + " Exist: " + (retval != null));
        if (retval == null) {
            try {
                Compound compound = createRootCompound(sos);
                IGUID compoundGUID = compound.getContentGUID();
                VersionBuilder builder = new VersionBuilder(compoundGUID).setInvariant(rootGUID);

                retval = sos.addVersion(builder);
                IGUID versionGUID = retval.getVersionGUID();
                sos.setHEAD(versionGUID);
            } catch (ManifestNotMadeException | ManifestPersistException | HEADNotSetException e) {
                e.printStackTrace();
            }
        }

        return retval;
    }

    private Asset getRoot(Agent sos, IGUID root) {
        Asset retval;
        try {
            retval = sos.getHEAD(root);
        } catch (HEADNotFoundException e) {
            return null;
        }
        return retval;
    }

    private Compound createRootCompound(Agent sos) throws ManifestPersistException, ManifestNotMadeException {
        return sos.addCompound(CompoundType.COLLECTION, Collections.emptyList());
    }

}
