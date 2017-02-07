package uk.ac.standrews.cs.sos.filesystem;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.FileSystemCreationException;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemFactory;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.impl.SOSFileSystem;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.builders.AssetBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.*;
import java.util.Collections;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFileSystemFactory implements IFileSystemFactory {

    public static final String WEBDAV_PATH = System.getProperty("user.home") + "/webdav/";
    public static final String WEBDAV_CURRENT_PATH = WEBDAV_PATH + "current/";

    private Agent agent;
    private IGUID rootGUID;

    private static CurrentAsset currentAsset;

    public SOSFileSystemFactory(Agent agent, IGUID rootGUID) {
        this(rootGUID);

        this.agent = agent;
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

        // Create directories for Webdav info
        File dir = new File(WEBDAV_CURRENT_PATH);
        dir.mkdirs();

        if (agent != null) {
            Asset rootAsset = getRoot(agent, rootGUID);
            if (rootAsset == null) {
                rootAsset = createRoot(agent);
            }

            return new SOSFileSystem(agent, rootAsset);
        } else {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Unable to create file system");
            return null;
        }
    }

    private Asset createRoot(Agent sos) throws FileSystemCreationException {

        try {
            Compound compound = createRootCompound(sos);
            IGUID compoundGUID = compound.getContentGUID();
            AssetBuilder builder = new AssetBuilder(compoundGUID).setInvariant(rootGUID);

            Asset retval = sos.addAsset(builder);
            IGUID versionGUID = retval.getVersionGUID();

            WriteCurrentVersion(rootGUID, versionGUID);

            return retval;
        } catch (ManifestNotMadeException | ManifestPersistException | FileNotFoundException e) {
            throw new FileSystemCreationException("WEBDAV - Unable to create Root Asset");
        }

    }

    public static Asset getRoot(Agent sos, IGUID root) {
        Asset retval = null;
        try {
            if (currentAsset == null) {
                IGUID version = ReadCurrentVersion(root);
                currentAsset = new CurrentAsset(root, version);
            }

            retval = (Asset) sos.getManifest(currentAsset.getVersion());

        } catch (GUIDGenerationException | IOException | ManifestNotFoundException e) { /* Ignore */ }

        return retval;
    }

    private Compound createRootCompound(Agent sos) throws ManifestPersistException, ManifestNotMadeException {
        return sos.addCompound(CompoundType.COLLECTION, Collections.emptySet());
    }

    public static void WriteCurrentVersion(IGUID invariant, IGUID version) throws FileNotFoundException {

        currentAsset = new CurrentAsset(invariant, version);

        try (PrintWriter out = new PrintWriter(WEBDAV_CURRENT_PATH + invariant)){
            out.println(version.toString());
        }
    }

    private static IGUID ReadCurrentVersion(IGUID invariant) throws IOException, GUIDGenerationException {

        File current = new File(WEBDAV_CURRENT_PATH + invariant);
        if (!current.exists()) {
            throw new IOException();
        }

        try(BufferedReader Buff = new BufferedReader(new FileReader(WEBDAV_CURRENT_PATH + invariant))) {
            String text = Buff.readLine();
            return GUIDFactory.recreateGUID(text);
        }
    }
}
