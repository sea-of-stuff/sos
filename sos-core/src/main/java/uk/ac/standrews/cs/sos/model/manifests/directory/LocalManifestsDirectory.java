package uk.ac.standrews.cs.sos.model.manifests.directory;

import org.apache.commons.io.IOUtils;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory implements ManifestsDirectory {

    private final static String BACKUP_EXTENSION = ".bak";

    final private LocalStorage localStorage;

    /**
     * Creates a manifests directory given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param localStorage
     */
    public LocalManifestsDirectory(LocalStorage localStorage) {
        this.localStorage = localStorage;
    }

    /**
     * Adds a manifest to the sea of stuff.
     *
     * @param manifest to be added to the sea of stuff
     */
    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        if (manifest.isValid()) {
            try {
                saveManifest(manifest);
            } catch (ManifestManagerException e) {
                e.printStackTrace();
            }
        } else {
            throw new ManifestPersistException("Manifest not valid");
        }
    }

    @Override
    public void updateAtom(Atom atom) throws ManifestManagerException, ManifestNotFoundException {

        IGUID manifestFileGUID = atom.guid();
        saveExistingManifest(manifestFileGUID, atom);
    }

    /**
     * Find a manifest in the SOS given a GUID.
     *
     * @param guid of the manifest to be found
     * @return Manifest
     * @throws ManifestNotFoundException
     */
    @Override
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        if (guid == null) {
            throw new ManifestNotFoundException("Cannot find manifest for null guid");
        }

        return getManifestFromGUID(guid);
    }

    // FIXME - this must be improved
    // Making a list and converting it to a stream is not really optimal.
    @Override
    public Stream<Manifest> getAllManifests() {

        Path dir = null;
        try {
            dir = localStorage.getManifestDirectory().toFile().toPath();
        } catch (IOException | DataStorageException e) {
            e.printStackTrace();
        }

        List<Manifest> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            for (Path entry: stream) {

                String filename = entry.getFileName().toString();
                filename = filename.substring(0, filename.length() - 5); // removing extension
                Manifest manifest = getManifestFromGUID(GUIDFactory.recreateGUID(filename));

                result.add(manifest);
            }
        } catch (DirectoryIteratorException | IOException | GUIDGenerationException | ManifestNotFoundException ex) {
            ex.printStackTrace();
        }

        return result.stream();
    }

    /**
     * Return the local HEAD for a given version
     *
     * // TODO - see what git/hg do
     * @param invariant for an asset
     * @return head version of the asset
     */
    @Override
    public Version getHEAD(IGUID invariant) throws HEADNotFoundException {

        try {
            File file = getHEADFile(invariant);
            Data data = file.getData();

            if (data == null || data.getSize() == 0) {
                throw new HEADNotFoundException("Unable to find head for asset " + invariant);
            }

            String str = IOUtils.toString(data.getInputStream(),  StandardCharsets.UTF_8);
            IGUID versionGUID = GUIDFactory.recreateGUID(str);
            return (Version) getManifestFromGUID(versionGUID);

        } catch (DataStorageException | GUIDGenerationException | ManifestNotFoundException | DataException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Set the specified version as the head for the asset that it represents
     *
     * @param version
     */
    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {

        try {
            Version versionManifest = (Version) getManifestFromGUID(version);
            IGUID invariant = versionManifest.getInvariantGUID();
            File file = getHEADFile(invariant);
            file.setData(new StringData(version.toString()));
            file.persist();

        } catch (ManifestNotFoundException | DataStorageException | DataException
                | PersistenceException e) {
            throw new HEADNotSetException("Unable to set the head for version " + version);
        }
    }

    @Override
    public void flush() {

    }

    private File getHEADFile(IGUID invariant) throws DataStorageException {
        Directory headsDir = localStorage.getHeadsDirectory();
        File file = localStorage.createFile(headsDir, invariant.toString());

        return file;
    }

    private Manifest getManifestFromGUID(IGUID guid) throws ManifestNotFoundException {
        File manifestFile = getManifestFile(guid);
        Manifest manifest = ManifestsUtils.ManifestFromFile(manifestFile);

        return manifest;
    }

    private void saveManifest(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestFileGUID = manifest.guid();

            boolean isAtomManifest = manifest.getManifestType().equals(ManifestType.ATOM);
            boolean manifestExists = manifestExistsInStorage(manifestFileGUID);

            if (isAtomManifest && manifestExists) {
                saveExistingAtomManifest(manifest);
            } else if (manifestExists) {
                saveExistingManifest(manifestFileGUID, manifest);
            } else {
                saveToFile(manifest);
            }

        } catch (ManifestNotFoundException e) {
            throw new ManifestManagerException(e);
        }

    }

    private void saveExistingAtomManifest(Manifest manifest) throws ManifestNotFoundException, ManifestManagerException {
        IGUID guid = manifest.getContentGUID();
        Manifest existingManifest = getManifestFromGUID(guid);
        mergeAtomManifestAndSave(existingManifest, manifest);
    }

    private void saveExistingManifest(IGUID manifestFileGUID, Manifest manifest) throws ManifestManagerException, ManifestNotFoundException {
        File manifestFile = getManifestFile(manifestFileGUID);
        File backupFile = backupManifest(manifest);
        FileHelper.DeleteFile(manifestFile);

        saveToFile(manifest);

        FileHelper.DeleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestManagerException {
        IGUID guid = manifest.getContentGUID();

        try {
            File manifestFile = getManifestFile(guid);
            File backupFile = backupManifest(existingManifest);

            if (!existingManifest.equals(manifest)) {
                manifest = mergeManifests(guid, (Atom) existingManifest, (Atom) manifest);
                FileHelper.DeleteFile(manifestFile);
                saveToFile(manifest);
            }

            FileHelper.DeleteFile(backupFile);
        } catch (ManifestNotFoundException e) {
            throw new ManifestManagerException("Manifests " + existingManifest.getContentGUID().toString()
                    + " and " + manifest.getContentGUID().toString() + "could not be merged", e);
        }

    }

    private File backupManifest(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestGUID = manifest.guid();
            File manifestFileToBackup = getManifestFile(manifestGUID);

            Directory manifestsDirectory = localStorage.getManifestDirectory();
            File backupManifest = localStorage.createFile(manifestsDirectory,
                    manifestFileToBackup.getName() + BACKUP_EXTENSION,
                    manifestFileToBackup.getData());
            backupManifest.persist();

            return backupManifest;
        } catch (ManifestNotFoundException | DataStorageException | DataException | PersistenceException e) {
            throw new ManifestManagerException("Manifest could not be backed up ", e);
        }

    }

    private void saveToFile(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestGUID = manifest.guid();
            File manifestFile = getManifestFile(manifestGUID.toString());

            Data manifestData = new StringData(manifest.toString());
            manifestFile.setData(manifestData);
            manifestFile.persist();
        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new ManifestManagerException(e);
        }
    }

    private File getManifestFile(IGUID guid) throws ManifestNotFoundException {
        try {
            return getManifestFile(guid.toString());
        } catch (DataStorageException e) {
            throw new ManifestNotFoundException("Unable to find manifest file for GUID: " + guid);
        }
    }

    private File getManifestFile(String guid) throws DataStorageException {
        Directory manifestsDir = localStorage.getManifestDirectory();
        File file = ManifestsUtils.ManifestFile(localStorage, manifestsDir, guid);

        return file;
    }


    private Manifest mergeManifests(IGUID guid, Atom first, Atom second) {
        HashSet<LocationBundle> locations = new HashSet<>();
        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    private boolean manifestExistsInStorage(IGUID guid) throws ManifestNotFoundException {
        File manifest = getManifestFile(guid);
        return manifest.exists();
    }
}
