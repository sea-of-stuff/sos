package uk.ac.standrews.cs.sos.model.manifests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestManagerException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Storage;
import uk.ac.standrews.cs.sos.utils.FileHelper;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Manage the manifests of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestsManager {

    private final static String BACKUP_EXTENSION = ".bak";
    private final static String JSON_EXTENSION = ".json";
    
    private static final int DEFAULT_RESULTS = 10;
    private static final int DEFAULT_SKIP_RESULTS = 0;

    final private Storage storage;
    final private Index index;

    /**
     * Creates a manifests manager given a sea of stuff configuration object and
     * a policy for the sea of stuff. The configuration object is need to know the
     * locations for the manifests.
     *
     * @param index used to record information for the manifests.
     */
    public ManifestsManager(Storage storage, Index index) {
        this.storage = storage;
        this.index = index;
    }

    /**
     * Adds a manifest to the sea of stuff.
     *
     * @param manifest to be added to the sea of stuff
     */
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
     * Find a manifest in the sea of stuff given a GUID.
     *
     * @param guid of the manifest to be found
     * @return Manifest
     * @throws ManifestNotFoundException
     */
    public Manifest findManifest(IGUID guid) throws ManifestNotFoundException {
        if (guid == null) {
            throw new ManifestNotFoundException();
        }

        Manifest manifest;
        try {
            manifest = getManifestFromFile(guid);
        } catch (ManifestManagerException ex) {
            throw new ManifestNotFoundException();
        }

        return manifest;
    }

    public Collection<IGUID> findManifestsByType(String type) throws ManifestNotFoundException {
        Collection<IGUID> retval;
        try {
            retval = index.getManifestsOfType(type, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    public Collection<IGUID> findVersions(IGUID guid) throws ManifestNotFoundException {
        Collection<IGUID> retval;
        try {
            retval = index.getVersions(guid, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    public Collection<IGUID> findManifestsThatMatchLabel(String label) throws ManifestNotFoundException {
        Collection<IGUID> retval;
        try {
            retval = index.getMetaLabelMatches(label, DEFAULT_RESULTS, DEFAULT_SKIP_RESULTS);
        } catch (IndexException e) {
            throw new ManifestNotFoundException();
        }
        return retval;
    }

    /**************************************************************************/
    /* PRIVATE METHODS */
    /**************************************************************************/

    private Manifest getManifestFromFile(IGUID guid) throws ManifestManagerException {
        Manifest manifest = null;
        SOSFile manifestFile = getManifestFile(guid);
        try {
            JsonNode node = JSONHelper.JsonObjMapper().readTree(manifestFile.toFile());
            String type = node.get(ManifestConstants.KEY_TYPE).textValue();

            manifest = constructManifestFromJson(type, manifestFile);
        } catch (FileNotFoundException | UnknownManifestTypeException | ManifestNotMadeException e) {
            throw new ManifestManagerException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return manifest;
    }

    private Manifest constructManifestFromJson(String type, SOSFile manifestData) throws UnknownManifestTypeException, ManifestNotMadeException {
        Manifest manifest = null;
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
                    throw new UnknownManifestTypeException();
            }
        } catch (IOException e) {
            throw new ManifestNotMadeException();
        }

        return manifest;
    }

    private void saveManifest(Manifest manifest) throws ManifestManagerException {
        if (manifest.getManifestType().equals(ManifestConstants.ATOM) &&
                manifestExistsInLocalStorage(manifest.getContentGUID())) {
            mergeAtomManifestAndSave(manifest);
        } else {
            saveToFile(manifest);
        }

        cacheManifest(manifest);
    }

    private void mergeAtomManifestAndSave(Manifest manifest) throws ManifestManagerException {
        IGUID guid = manifest.getContentGUID();
        Manifest existingManifest = getManifestFromFile(guid);
        SOSFile backupPath = backupManifest(existingManifest);

        if (!existingManifest.equals(manifest)) {
            manifest = mergeManifests(guid, (Atom) existingManifest, (Atom) manifest);
            FileHelper.deleteFile(backupPath.getPathname());
            saveToFile(manifest);
        }

        FileHelper.deleteFile(backupPath + BACKUP_EXTENSION);
    }

    private SOSFile backupManifest(Manifest manifest) throws ManifestManagerException {
        IGUID manifestGUID = getGUIDUsedToStoreManifest(manifest);
        SOSFile backupManifest = getManifestFile(manifestGUID);

        try {
            FileHelper.copyToFile(
                    new URILocation(backupManifest.getPathname()).getSource(),
                    backupManifest + BACKUP_EXTENSION);
        } catch (IOException | URISyntaxException e) {
            throw new ManifestManagerException("Manifest could not be merged", e);
        }

        return backupManifest;
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
        IGUID manifestGUID = getGUIDUsedToStoreManifest(manifest);
        SOSFile manifestFile = getManifestFile(manifestGUID.toString());

        FileHelper.touchDir(manifestFile);
        if (manifestFile.exists())
            return;

        writeToFile(manifestFile.toFile(), manifest);
    }

    private void writeToFile(File file, Manifest manifest) throws ManifestManagerException {
        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(manifest.toString());
        } catch (Exception e) {
            throw new ManifestManagerException("Manifest could not be written to file");
        } finally {
            file.setReadOnly();
        }
    }

    private void cacheManifest(Manifest manifest) throws ManifestManagerException {
        try {
            index.addManifest(manifest);
        } catch (IndexException e) {
            throw new ManifestManagerException(e);
        }
    }

    private SOSFile getManifestFile(IGUID guid) throws ManifestManagerException {
        return getManifestFile(guid.toString());
    }

    private SOSFile getManifestFile(String guid) throws ManifestManagerException {
        try {
            SOSDirectory manifestsDir = Configuration.getInstance()
                    .getManifestsDirectory();

            return storage.createFile(manifestsDir, normaliseGUID(guid));
        } catch (ConfigurationException e) {
            throw new ManifestManagerException();
        }
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

    private boolean manifestExistsInLocalStorage(IGUID guid) throws ManifestManagerException {
        SOSFile path = getManifestFile(guid);
        return path.exists();
    }

}
