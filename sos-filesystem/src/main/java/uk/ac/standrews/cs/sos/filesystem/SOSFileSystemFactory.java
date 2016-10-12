package uk.ac.standrews.cs.sos.filesystem;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.configuration.SOSConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.filesystem.impl.SOSFileSystem;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.node.LocalNode;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.StorageFactory;
import uk.ac.standrews.cs.storage.StorageType;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.File;
import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    private String configurationPath;
    private IGUID rootGUID;

    private InternalStorage internalStorage;

    public SOSFileSystemFactory(String configurationPath, IGUID rootGUID) {
        this.configurationPath = configurationPath;
        this.rootGUID = rootGUID;
    }

    public SOSFileSystemFactory(IGUID rootGUID) {
        this.rootGUID = rootGUID;
    }

    @Override
    public IFileSystem makeFileSystem() throws FileSystemCreationException {
        Client client = makeSOSClient();
        return makeFileSystem(client);
    }

    public IFileSystem makeFileSystem(Client client) throws FileSystemCreationException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Factory - Making the File System");

        if (client != null) {
            Version rootAsset = createRoot(client);
            return new SOSFileSystem(client, rootAsset);
        } else {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Unable to create file system");
            return null;
        }
    }

    private Client makeSOSClient() {
        Client client = null;
        try {
            SOSConfiguration configuration = createConfiguration();
            createNodeDependencies(configuration);

            SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
            LocalNode localSOSNode = builder
                    .configuration(configuration)
                    .internalStorage(internalStorage)
                    .build();

            client = localSOSNode.getClient();
        } catch (GUIDGenerationException | SOSException e) {
            e.printStackTrace();
        }

        return client;
    }

    private SOSConfiguration createConfiguration() throws SOSConfigurationException {
        File file = new File(configurationPath);
        return new SOSConfiguration(file);
    }

    private void createNodeDependencies(SOSConfiguration configuration) throws SOSException {
        try {

            StorageType storageType = configuration.getStorageType();
            String root = configuration.getStorageLocation();

            internalStorage = new InternalStorage(StorageFactory
                            .createStorage(storageType, root, false));
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

    }

    private Version createRoot(Client sos) {
        Version retval = getRoot(sos, rootGUID);

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

    private Version getRoot(Client sos, IGUID root) {
        Version retval;
        try {
            retval = sos.getHEAD(root);
        } catch (HEADNotFoundException e) {
            return null;
        }
        return retval;
    }

    private Compound createRootCompound(Client sos) throws ManifestPersistException, ManifestNotMadeException {
        return sos.addCompound(CompoundType.COLLECTION, Collections.emptyList());
    }

}
