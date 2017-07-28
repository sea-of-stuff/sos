package uk.ac.standrews.cs.sos.impl.manifests.directory;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.castore.exceptions.DataException;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.BASE;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.FileUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * IDirectory for the manifests stored locally to this node
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocalManifestsDirectory extends AbstractManifestsDirectory {

    private final static String BACKUP_EXTENSION = ".bak";
    private final static String TIP_TAG = "TIP-";
    private final static String HEAD_TAG = "HEAD-";

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
    public Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException {

        try {
            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            String filename = TIP_TAG + invariant.toMultiHash(BASE.HEX);

            // Make sure that the tip file exists
            IFile file = localStorage.createFile(manifestsDir, filename);
            if (!file.exists()) {
                throw new TIPNotFoundException();
            }

            String content = FileUtils.FileContent(localStorage, manifestsDir, filename);
            List<String> versions = content.isEmpty() ? new LinkedList<>() : Arrays.asList(content.split("\n"));

            Set<IGUID> versionsRefs =  versions.stream()
                    .map(v -> {
                        try {
                            return GUIDFactory.recreateGUID(v);
                        } catch (GUIDGenerationException e) {
                            return new InvalidID();
                        }
                    }).collect(Collectors.toSet());

            return versionsRefs;

        } catch (DataException | DataStorageException e) {
            throw new TIPNotFoundException();
        }
    }

    @Override
    public IGUID getHead(IGUID invariant) throws HEADNotFoundException {

        try {

            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            String filename = HEAD_TAG + invariant.toMultiHash(BASE.HEX);

            String guid = FileUtils.FileContent(localStorage, manifestsDir, filename);
            guid = guid.replace("\n", "");
            return GUIDFactory.recreateGUID(guid);

        } catch (DataStorageException | DataException | GUIDGenerationException e) {
            throw new HEADNotFoundException();
        }

    }

    @Override
    public void setHead(Version version) {

        try {

            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            String filename = HEAD_TAG + version.getInvariantGUID().toMultiHash(BASE.HEX);

            IFile file = FileUtils.CreateFileWithContent(localStorage, manifestsDir, filename, version.getInvariantGUID().toMultiHash(BASE.HEX));
            file.persist();

        } catch (DataStorageException | PersistenceException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void flush() {}

    private Manifest getManifestFromGUID(IGUID guid) throws ManifestNotFoundException {
        IFile manifestFile = getManifestFile(guid);

        return FileUtils.ManifestFromFile(manifestFile);
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
        FileUtils.DeleteFile(manifestFile);

        saveToFile(manifest);

        FileUtils.DeleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestsDirectoryException {
        IGUID guid = manifest.guid();

        try {
            IFile manifestFile = getManifestFile(guid);

            IFile backupFile = backupManifest(existingManifest);

            if (!existingManifest.equals(manifest)) {
                manifest = mergeManifests(guid, (Atom) existingManifest, (Atom) manifest);
                FileUtils.DeleteFile(manifestFile);
                saveToFile(manifest);
            }

            FileUtils.DeleteFile(backupFile);
        } catch (ManifestNotFoundException e) {
            throw new ManifestsDirectoryException("Manifests " + existingManifest.guid().toMultiHash(BASE.HEX) + " and " + manifest.guid().toMultiHash(BASE.HEX) + "could not be merged", e);
        }

    }

    private IFile backupManifest(Manifest manifest) throws ManifestsDirectoryException {

        try {
            IGUID manifestGUID = manifest.guid();
            IFile manifestFileToBackup = getManifestFile(manifestGUID);

            IDirectory manifestsDirectory = localStorage.getManifestsDirectory();
            IFile backupManifest = localStorage.createFile(manifestsDirectory, manifestFileToBackup.getName() + BACKUP_EXTENSION, manifestFileToBackup.getData());
            backupManifest.persist();

            return backupManifest;
        } catch (ManifestNotFoundException | DataStorageException | DataException | PersistenceException e) {
            throw new ManifestsDirectoryException("Manifest could not be backed up ", e);
        }

    }

    private void saveToFile(Manifest manifest) throws ManifestsDirectoryException {

        try {
            String manifestGUID = manifest.guid().toMultiHash(BASE.HEX);
            IFile manifestTempFile = getManifestTempFile(manifestGUID);

            Data manifestData = new StringData(manifest.toString());
            manifestTempFile.setData(manifestData);
            manifestTempFile.persist();

            manifestTempFile.rename(manifestGUID + FileUtils.JSON_EXTENSION);

        } catch (PersistenceException | DataException | DataStorageException | RenameException e) {
            throw new ManifestsDirectoryException(e);
        }
    }

    private IFile getManifestFile(IGUID guid) throws ManifestNotFoundException {
        try {
            return getManifestFile(guid.toMultiHash(BASE.HEX));
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

    public void advanceTip(IGUID invariant, IGUID version) {

        appendTip(invariant, version);
    }

    public void advanceTip(IGUID invariant, Set<IGUID> previousVersions, IGUID newVersion) {

        Set<String> previousVersionsStrings = previousVersions.stream()
                .map(g -> g.toMultiHash(BASE.HEX))
                .collect(Collectors.toSet());

        appendTip(invariant, newVersion);
        removeTips(invariant, previousVersionsStrings);
    }

    /**
     * Append a version to the tip file for the specified invariant
     * @param invariant
     * @param version
     */
    private void appendTip(IGUID invariant, IGUID version) {

        try {

            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            String filename = TIP_TAG + invariant.toMultiHash(BASE.HEX);

            // Make sure that the tip file exists
            IFile file = localStorage.createFile(manifestsDir, filename);
            if (!file.exists()) {
                file.persist();
            }

            String newContent;
            try {
                String content = FileUtils.FileContent(localStorage, manifestsDir, filename);
                Set<String> versions = content.isEmpty() ? new LinkedHashSet<>() : new LinkedHashSet<>(Arrays.asList(content.split("\n")));
                versions.add(version.toMultiHash(BASE.HEX));

                newContent = versions.stream().collect(Collectors.joining( "\n" ));

            } catch (DataStorageException | DataException e) {
                newContent = version.toMultiHash(BASE.HEX);
            }

            IFile fileWithContent = FileUtils.CreateFileWithContent(localStorage, manifestsDir, filename, newContent);
            fileWithContent.persist();

        } catch (DataStorageException | PersistenceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the specified versions from the HEAD file for the specified invariant
     * @param invariant
     * @param versionsToRemove
     */
    private void removeTips(IGUID invariant, Set<String> versionsToRemove) {

        try {

            IDirectory manifestsDir = localStorage.getManifestsDirectory();
            String filename = TIP_TAG + invariant.toMultiHash(BASE.HEX);

            String content = FileUtils.FileContent(localStorage, manifestsDir, filename);
            Set<String> versions = content.isEmpty() ? new LinkedHashSet<>() : new LinkedHashSet<>(Arrays.asList(content.split("\n")));
            versions.removeAll(versionsToRemove);

            String newContent = versions.stream().collect(Collectors.joining( "\n" ));

            IFile fileWithContent = FileUtils.CreateFileWithContent(localStorage, manifestsDir, filename, newContent);
            fileWithContent.persist();

        } catch (DataException | DataStorageException | PersistenceException e) {
            e.printStackTrace();
        }
    }

}
