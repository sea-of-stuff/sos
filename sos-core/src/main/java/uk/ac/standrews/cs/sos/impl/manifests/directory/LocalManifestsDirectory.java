package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.FileHelper;

import java.util.HashSet;

/**
 * IDirectory for the manifests stored locally to this node
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
     * @param localStorage local storage used by this node
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

        try {
            if (manifest.isValid()) {
                saveManifest(manifest);
            } else {
                throw new ManifestPersistException("Manifest not valid");
            }
        } catch (ManifestsDirectoryException e) {
            throw new ManifestPersistException("Unable to save manifest " + manifest);
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
        IFile manifestFile = getManifestFile(guid);

        return ManifestsUtils.ManifestFromFile(manifestFile);
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
        IFile manifestFile = getManifestFile(manifestFileGUID);
        IFile backupFile = backupManifest(manifest);
        FileHelper.DeleteFile(manifestFile);

        saveToFile(manifest);

        FileHelper.DeleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestsDirectoryException {
        IGUID guid = manifest.guid();

        try {
            IFile manifestFile = getManifestFile(guid);

            IFile backupFile = backupManifest(existingManifest);

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

    private IFile backupManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestGUID = manifest.guid();
            IFile manifestFileToBackup = getManifestFile(manifestGUID);

            IDirectory manifestsDirectory = localStorage.getManifestsDirectory();
            IFile backupManifest = localStorage.createFile(manifestsDirectory,
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
            IFile manifestTempFile = getManifestTempFile(manifestGUID);

            Data manifestData = new StringData(manifest.toString());
            manifestTempFile.setData(manifestData);
            manifestTempFile.persist();

            IFile manifestFile = getManifestFile(manifestGUID);
            FileHelper.RenameFile(manifestTempFile, manifestFile);
        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new ManifestsDirectoryException(e);
        }
    }

    private IFile getManifestFile(IGUID guid) throws ManifestNotFoundException {
        try {
            return getManifestFile(guid.toString());
        } catch (DataStorageException e) {
            throw new ManifestNotFoundException("Unable to find manifest file for GUID: " + guid);
        }
    }

    private IFile getManifestFile(String guid) throws DataStorageException {
        IDirectory manifestsDir = localStorage.getManifestsDirectory();

        return ManifestsUtils.ManifestFile(localStorage, manifestsDir, guid);
    }

    private IFile getManifestTempFile(String guid) throws DataStorageException {
        IDirectory manifestsDir = localStorage.getManifestsDirectory();

        return ManifestsUtils.ManifestTempFile(localStorage, manifestsDir, guid);
    }


    private Manifest mergeManifests(IGUID guid, Atom first, Atom second) {
        HashSet<LocationBundle> locations = new HashSet<>();
        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    private boolean manifestExistsInStorage(IGUID guid) throws ManifestNotFoundException {
        IFile manifest = getManifestFile(guid);
        return manifest.exists();
    }
}
