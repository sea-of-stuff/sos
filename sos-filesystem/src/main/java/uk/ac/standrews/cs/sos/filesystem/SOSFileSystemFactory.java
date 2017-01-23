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
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
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

        if (agent != null) {
            // Create directories for Webdav info
            File dir = new File(WEBDAV_CURRENT_PATH);
            dir.mkdirs();

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

                WriteCurrentVersion(rootGUID, versionGUID);
            } catch (ManifestNotMadeException | ManifestPersistException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return retval;
    }

    public static Asset getRoot(Agent sos, IGUID root) {
        Asset retval = null;
        try {
            IGUID version = ReadCurrentVersion(root);
            retval = (Asset) sos.getManifest(version);

        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return retval;
    }

    private Compound createRootCompound(Agent sos) throws ManifestPersistException, ManifestNotMadeException {
        return sos.addCompound(CompoundType.COLLECTION, Collections.emptySet());
    }

    public static void WriteCurrentVersion(IGUID invariant, IGUID version) throws FileNotFoundException {

        try (PrintWriter out = new PrintWriter(WEBDAV_CURRENT_PATH + invariant)){
            out.println(version.toString());
        }
    }

    public static IGUID ReadCurrentVersion(IGUID invariant) throws IOException, GUIDGenerationException {

        try(BufferedReader Buff = new BufferedReader(new FileReader(WEBDAV_CURRENT_PATH + invariant))) {
            String text = Buff.readLine();
            return GUIDFactory.recreateGUID(text);
        }
    }
}
