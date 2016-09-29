package uk.ac.standrews.cs.sos.model.manifests.managers;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.manifests.managers.ManifestsManager;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.storage.InternalStorage;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.data.Data;
import uk.ac.standrews.cs.storage.data.StringData;
import uk.ac.standrews.cs.storage.exceptions.DataException;
import uk.ac.standrews.cs.storage.exceptions.PersistenceException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
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
public class LocalManifestsManager implements ManifestsManager {

    private final static String BACKUP_EXTENSION = ".bak";
    private final static String JSON_EXTENSION = ".json";

    private static final int DEFAULT_RESULTS = 10;
    private static final int DEFAULT_SKIP_RESULTS = 0;

    final private InternalStorage internalStorage;

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param internalStorage
     */
    public LocalManifestsManager(InternalStorage internalStorage) {
        this.internalStorage = internalStorage;
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

        return getManifestFromFile(guid);
    }

    // FIXME - this must be improved
    // Making a list and converting it to a stream is not really optimal.
    @Override
    public Stream<Manifest> getAllManifests() {

        Path dir = null;
        try {
            dir = internalStorage.getManifestDirectory().toFile().toPath();
        } catch (IOException | DataStorageException e) {
            e.printStackTrace();
        }

        List<Manifest> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            for (Path entry: stream) {

                String filename = entry.getFileName().toString();
                filename = filename.substring(0, filename.length() - 5); // removing extension
                Manifest manifest = getManifestFromFile(GUIDFactory.recreateGUID(filename));

                result.add(manifest);
            }
        } catch (DirectoryIteratorException | IOException | GUIDGenerationException | ManifestNotFoundException ex) {
            ex.printStackTrace();
        }

        return result.stream();
    }

    /**
     * Return the local HEAD for a given version
     * @param invariant for an asset
     * @return head version of the asset
     */
    public Version getHEAD(IGUID invariant) {
        // TODO - see what git/hg do
        // see notes in the setHEAD method
        return null;
    }

    /**
     * Set the specified version as the head for the asset that it represents
     * @param version
     */
    public void setHEAD(IGUID version) {
        // reset current head
        // File name: invariant
        // Content file: version id

        // Optionally, use a db
    }

    private Manifest getManifestFromFile(IGUID guid) throws ManifestNotFoundException {

        try {
            File manifestFile = getManifestFile(guid);

            JsonNode node = JSONHelper.JsonObjMapper().readTree(manifestFile.toFile());
            String type = node.get(ManifestConstants.KEY_TYPE).textValue();

            Manifest manifest = constructManifestFromJson(type, manifestFile);

            return manifest;
        } catch (UnknownManifestTypeException | ManifestNotMadeException
                | IOException | DataStorageException e) {
            throw new ManifestNotFoundException("Unable to find manifest for GUID " + guid.toString(), e);
        }

    }

    private Manifest constructManifestFromJson(String type, File manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest;
        try {
            switch (type) {
                case ManifestConstants.ATOM:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), AtomManifest.class);
                    break;
                case ManifestConstants.COMPOUND:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), CompoundManifest.class);
                    break;
                case ManifestConstants.VERSION:
                    manifest = JSONHelper.JsonObjMapper().readValue(manifestData.toFile(), VersionManifest.class);
                    break;
                default:
                    throw new UnknownManifestTypeException("Manifest type " + type + " is unknown");
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException("Unable to create a manifest from file at " + manifestData.getPathname());
        }

        return manifest;
    }

    private void saveManifest(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestFileGUID = getGUIDUsedToStoreManifest(manifest);

            boolean isAtomManifest = manifest.getManifestType().equals(ManifestConstants.ATOM);
            boolean manifestExists = manifestExistsInStorage(manifestFileGUID);

            if (isAtomManifest && manifestExists) {
                saveExistingAtomManifest(manifest);
            } else if (manifestExists) {
                saveExistingManifest(manifestFileGUID, manifest);
            } else {
                saveToFile(manifest);
            }

        } catch (DataStorageException | ManifestNotFoundException e) {
            throw new ManifestManagerException(e);
        }

    }

    private void saveExistingAtomManifest(Manifest manifest) throws ManifestNotFoundException, ManifestManagerException {
        IGUID guid = manifest.getContentGUID();
        Manifest existingManifest = getManifestFromFile(guid);
        mergeAtomManifestAndSave(existingManifest, manifest);
    }

    private void saveExistingManifest(IGUID manifestFileGUID, Manifest manifest) throws DataStorageException, ManifestManagerException {
        File manifestFile = getManifestFile(manifestFileGUID);
        File backupFile = backupManifest(manifest);
        FileHelper.deleteFile(manifestFile);

        saveToFile(manifest);

        FileHelper.deleteFile(backupFile);
    }

    private void mergeAtomManifestAndSave(Manifest existingManifest, Manifest manifest) throws ManifestManagerException {
        IGUID guid = manifest.getContentGUID();

        try {
            File manifestFile = getManifestFile(guid);
            File backupFile = backupManifest(existingManifest);

            if (!existingManifest.equals(manifest)) {
                manifest = mergeManifests(guid, (Atom) existingManifest, (Atom) manifest);
                FileHelper.deleteFile(manifestFile);
                saveToFile(manifest);
            }

            FileHelper.deleteFile(backupFile);
        } catch (DataStorageException e) {
            throw new ManifestManagerException("Manifests " + existingManifest.getContentGUID().toString()
                    + " and " + manifest.getContentGUID().toString() + "could not be merged", e);
        }

    }

    private File backupManifest(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestGUID = getGUIDUsedToStoreManifest(manifest);
            File manifestFileToBackup = getManifestFile(manifestGUID);

            Directory manifestsDirectory = internalStorage.getManifestDirectory();
            File backupManifest = internalStorage.createFile(manifestsDirectory,
                    manifestFileToBackup.getName() + BACKUP_EXTENSION,
                    manifestFileToBackup.getData());
            backupManifest.persist();

            return backupManifest;
        } catch (DataStorageException | DataException | PersistenceException e) {
            throw new ManifestManagerException("Manifest could not be backed up ", e);
        }

    }

    private IGUID getGUIDUsedToStoreManifest(Manifest manifest) {
        IGUID guid;

        if (manifest.getManifestType().equals(ManifestConstants.VERSION)) {
            guid = ((VersionManifest) manifest).getVersionGUID();
        } else {
            guid = manifest.getContentGUID();
        }

        return guid;
    }

    private void saveToFile(Manifest manifest) throws ManifestManagerException {

        try {
            IGUID manifestGUID = getGUIDUsedToStoreManifest(manifest);
            File manifestFile = getManifestFile(manifestGUID.toString());

            Data manifestData = new StringData(manifest.toString());
            manifestFile.setData(manifestData);
            manifestFile.persist();
        } catch (PersistenceException | DataException | DataStorageException e) {
            throw new ManifestManagerException(e);
        }
    }

    private File getManifestFile(IGUID guid) throws DataStorageException {
        return getManifestFile(guid.toString());
    }

    private File getManifestFile(String guid) throws DataStorageException {
        Directory manifestsDir = internalStorage.getManifestDirectory();
        return internalStorage.createFile(manifestsDir, normaliseGUID(guid));
    }

    private String normaliseGUID(String guid) {
        return guid + JSON_EXTENSION;
    }

    private Manifest mergeManifests(IGUID guid, Atom first, Atom second) {
        HashSet<LocationBundle> locations = new HashSet<>();
        locations.addAll(first.getLocations());
        locations.addAll(second.getLocations());

        return ManifestFactory.createAtomManifest(guid, locations);
    }

    private boolean manifestExistsInStorage(IGUID guid) throws DataStorageException {
        File manifest = getManifestFile(guid);
        return manifest.exists();
    }
}
