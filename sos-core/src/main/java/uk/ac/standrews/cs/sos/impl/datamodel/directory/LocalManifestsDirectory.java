package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestParam;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.SecureAtom;
import uk.ac.standrews.cs.sos.utils.FileUtils;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * IDirectory for the manifests stored locally to this node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory extends AbstractManifestsDirectory {

    private static final String BACKUP_EXTENSION = ".bak";

    final private LocalStorage localStorage;

    /**
     * Creates a manifests directory given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param localStorage local storage used by this node
     */
    public LocalManifestsDirectory(final LocalStorage localStorage) {
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
    public void delete(IGUID guid) throws ManifestNotFoundException {

        try {
            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            manifestsDir.remove(guid.toMultiHash());
        } catch (DataStorageException | BindingAbsentException e) {
            throw new ManifestNotFoundException("Manifest with GUID "  + guid.toMultiHash() + " was not found and could not be deleted.");
        }
    }

    @Override
    public void flush() {}

    public Set<IGUID> getManifests(Set<IGUID> input, List<ManifestParam> params) {

        Set<IGUID> matchedManifestRefs = new LinkedHashSet<>();

        for(IGUID manifestRef:input) {

            try {
                String manifest = getManifestFromGUID(manifestRef).toString();
                JsonNode jsonNode = JSONHelper.jsonObjMapper().readTree(manifest);

                // Search
                int matches = 0;
                for(ManifestParam param:params) {

                    if (jsonNode.has(param.getType())) {

                        JsonNode field = jsonNode.get(param.getType());
                        if (field.asText().equals(param.getValue())) {
                            matches++;
                        }
                    }
                }

                if (matches == params.size()) {
                    matchedManifestRefs.add(manifestRef);
                }

            } catch (ManifestNotFoundException | IOException e) {
                SOS_LOG.log(LEVEL.WARN, "Unable to check manifest with GUID " + manifestRef.toMultiHash());
            }
        }

        return matchedManifestRefs;
    }

    private Manifest getManifestFromGUID(IGUID guid) throws ManifestNotFoundException {
        IFile manifestFile = getManifestFile(guid);

        return FileUtils.ManifestFromFile(manifestFile);
    }

    private void saveManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestFileGUID = manifest.guid();

            boolean isAtomManifest = manifest.getType().equals(ManifestType.ATOM) || manifest.getType().equals(ManifestType.ATOM_PROTECTED);
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
        FileUtils.DeleteFile(manifestFile);
        // TODO - merge ops
        saveToFile(manifest);

        FileUtils.DeleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestsDirectoryException {
        IGUID guid = manifest.guid();

        try {
            IFile manifestFile = getManifestFile(guid);
            IFile backupFile = backupManifest(existingManifest);

            if (!existingManifest.equals(manifest)) {

                if (manifest.getType().equals(ManifestType.ATOM)) {
                    manifest = mergeManifests(guid, (Atom) existingManifest, (Atom) manifest);
                } else if (manifest.getType().equals(ManifestType.ATOM_PROTECTED)) {
                    manifest = mergeManifests(guid, (SecureAtom) existingManifest, (SecureAtom) manifest);
                }

                FileUtils.DeleteFile(manifestFile);
                saveToFile(manifest);
            }

            FileUtils.DeleteFile(backupFile);
        } catch (ManifestNotFoundException e) {
            throw new ManifestsDirectoryException("Manifests " + existingManifest.guid().toMultiHash() + " and " + manifest.guid().toMultiHash() + "could not be merged", e);
        }

    }

    private IFile backupManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestGUID = manifest.guid();
            IFile manifestFileToBackup = getManifestFile(manifestGUID);

            IDirectory manifestsDirectory = localStorage.getManifestsDirectory();

            try (Data manifestFileData = manifestFileToBackup.getData()) {
                IFile backupManifest = localStorage.createFile(manifestsDirectory, manifestFileToBackup.getName() + BACKUP_EXTENSION, manifestFileData);
                backupManifest.persist();
                return backupManifest;

            } catch (DataException | IOException e) {
                throw new PersistenceException("Unable to persist manifest", e);
            }

        } catch (ManifestNotFoundException | DataStorageException | PersistenceException e) {
            throw new ManifestsDirectoryException("Manifest could not be backed up ", e);
        }

    }

    private void saveToFile(Manifest manifest) throws ManifestsDirectoryException {

        try {
            String manifestGUID = manifest.guid().toMultiHash();
            IFile manifestTempFile = getManifestTempFile(manifestGUID);

            try (Data manifestData = new StringData(manifest.toString())) {
                manifestTempFile.setData(manifestData);
                manifestTempFile.persist();

                manifestTempFile.rename(manifestGUID + FileUtils.JSON_EXTENSION);
            } catch (DataException | IOException | RenameException e) {
                throw new PersistenceException("Unabel to persist renamed manifest", e);
            }

        } catch (PersistenceException | DataStorageException e) {
            throw new ManifestsDirectoryException(e);
        }
    }

    private IFile getManifestFile(IGUID guid) throws ManifestNotFoundException {
        try {
            return getManifestFile(guid.toMultiHash());
        } catch (DataStorageException e) {
            throw new ManifestNotFoundException("Unable to find manifest file for GUID: " + guid.toShortString());
        }
    }

    private IFile getManifestFile(String guid) throws DataStorageException {
        IDirectory manifestsDir = localStorage.getManifestsDirectory();

        return FileUtils.CreateFile(localStorage, manifestsDir, guid, FileUtils.JSON_EXTENSION);
    }

    private IFile getManifestTempFile(String guid) throws DataStorageException {
        IDirectory manifestsDir = localStorage.getManifestsDirectory();

        return FileUtils.CreateTempFile(localStorage, manifestsDir, guid);
    }

    private boolean manifestExistsInStorage(IGUID guid) throws ManifestNotFoundException {
        IFile manifest = getManifestFile(guid);
        return manifest.exists();
    }

}
