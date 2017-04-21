package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.storage.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.util.HashSet;

/**
 * Directory for the manifests stored locally to this node
 *
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
            } catch (ManifestsDirectoryException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to save manifest " + manifest);
            }
        } else {
            throw new ManifestPersistException("Manifest not valid");
        }

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
        if (guid == null || guid.isInvalid()) {
            throw new ManifestNotFoundException("Cannot find manifest for null guid");
        }

        return getManifestFromGUID(guid);
    }

    @Override
    public void flush() {}

    private Manifest getManifestFromGUID(IGUID guid) throws ManifestNotFoundException {
        File manifestFile = getManifestFile(guid);
        Manifest manifest = ManifestsUtils.ManifestFromFile(manifestFile);

        return manifest;
    }

    private void saveManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestFileGUID = manifest.guid();

            boolean isAtomManifest = manifest.getType().equals(ManifestType.ATOM);
            boolean manifestExists = manifestExistsInStorage(manifestFileGUID);

            if (isAtomManifest && manifestExists) {
                saveExistingAtomManifest(manifest);
            } else if (manifestExists) {
                saveExistingManifest(manifestFileGUID, manifest);
            } else {
                saveToFile(manifest);
            }

        } catch (ManifestNotFoundException e) {
            throw new ManifestsDirectoryException(e);
        }

    }

    private void saveExistingAtomManifest(Manifest manifest) throws ManifestNotFoundException, ManifestsDirectoryException {
        IGUID guid = manifest.guid();
        Manifest existingManifest = getManifestFromGUID(guid);
        mergeAtomManifestAndSave(existingManifest, manifest);
    }

    private void saveExistingManifest(IGUID manifestFileGUID, Manifest manifest) throws ManifestsDirectoryException, ManifestNotFoundException {
        File manifestFile = getManifestFile(manifestFileGUID);
        File backupFile = backupManifest(manifest);
        FileHelper.DeleteFile(manifestFile);

        saveToFile(manifest);

        FileHelper.DeleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestsDirectoryException {
        IGUID guid = manifest.guid();

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
            throw new ManifestsDirectoryException("Manifests " + existingManifest.guid().toString()
                    + " and " + manifest.guid().toString() + "could not be merged", e);
        }

    }

    private File backupManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestGUID = manifest.guid();
            File manifestFileToBackup = getManifestFile(manifestGUID);

            Directory manifestsDirectory = localStorage.getManifestsDirectory();
            File backupManifest = localStorage.createFile(manifestsDirectory,
                    manifestFileToBackup.getName() + BACKUP_EXTENSION,
                    manifestFileToBackup.getData());
            backupManifest.persist();

            return backupManifest;
        } catch (ManifestNotFoundException | DataStorageException | DataException | PersistenceException e) {
            throw new ManifestsDirectoryException("Manifest could not be backed up ", e);
        }

    }

    private void saveToFile(Manifest manifest) throws ManifestsDirectoryException {

        try {
            String manifestGUID = manifest.guid().toString();
            File manifestTempFile = getManifestTempFile(manifestGUID);

            Data manifestData = new StringData(manifest.toString());
            manifestTempFile.setData(manifestData);
            manifestTempFile.persist();

            File manifestFile = getManifestFile(manifestGUID);
            FileHelper.RenameFile(manifestTempFile, manifestFile);
        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new ManifestsDirectoryException(e);
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
        Directory manifestsDir = localStorage.getManifestsDirectory();
        File file = ManifestsUtils.ManifestFile(localStorage, manifestsDir, guid);

        return file;
    }

    private File getManifestTempFile(String guid) throws DataStorageException {
        Directory manifestsDir = localStorage.getManifestsDirectory();
        File file = ManifestsUtils.ManifestTempFile(localStorage, manifestsDir, guid);

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
