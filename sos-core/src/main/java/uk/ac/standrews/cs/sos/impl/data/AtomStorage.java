package uk.ac.standrews.cs.sos.impl.data;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.utilities.Pair;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;
import uk.ac.standrews.cs.utilities.crypto.SymmetricEncryption;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/*
 * This is the class that will take care of storing atom's data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorage {

    private IGUID localNodeGUID;
    private LocalStorage localStorage;

    public AtomStorage(IGUID localNodeGUID, LocalStorage storage) {
        this.localNodeGUID = localNodeGUID;
        this.localStorage = storage;
    }

    public StoredAtomInfo persist(AtomBuilder atomBuilder) throws DataStorageException {

        try {
            StoredAtomInfo storedAtomInfo = storeToLocalStorage(atomBuilder);
            Location localLocation = makeLocalSOSLocation(storedAtomInfo.getGuid());
            LocationBundle bundle = new LocationBundle(BundleTypes.PERSISTENT, localLocation);

            return storedAtomInfo.setLocationBundle(bundle);

        } catch (SourceLocationException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    // Data is stored in disk, but marked as cached
    public StoredAtomInfo cache(AtomBuilder atomBuilder) throws DataStorageException {

        try {
            StoredAtomInfo storedAtomInfo = storeToLocalStorage(atomBuilder);
            Location localLocation = makeLocalSOSLocation(storedAtomInfo.getGuid());
            LocationBundle bundle = new LocationBundle(BundleTypes.CACHE, localLocation);

            return storedAtomInfo.setLocationBundle(bundle);

        } catch (SourceLocationException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    public Data decryptData(Data encryptedData, SecretKey decryptedKey) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SymmetricEncryption.decrypt(decryptedKey, encryptedData.getInputStream(), out);
            return new InputStreamData(IO.OutputStreamToInputStream(out));

        } catch (CryptoException | IOException e) {
            throw new ProtectionException(e);
        }

    }

    private StoredAtomInfo storeToLocalStorage(AtomBuilder atomBuilder) throws DataStorageException {

        if (!atomBuilder.isBuildIsSet()) {
            throw new DataStorageException("AtomBuilder not set correctly");
        }

        // Store data first and then assign valid GUID
        try {
            IGUID tmpGUID = GUIDFactory.generateRandomGUID();
            StoredAtomInfo storedAtomInfo = persistData(tmpGUID, atomBuilder);

            IFile tmpCachedLocation = atomFileInLocalStorage(tmpGUID);
            IGUID guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));
            tmpCachedLocation.rename(guid.toMultiHash());

            return storedAtomInfo.setGuid(guid);

        } catch (RenameException | URISyntaxException e) {
            throw new DataStorageException("Unable to persist data properly");
        }

    }

    private StoredAtomInfo persistData(IGUID tmpGUID, AtomBuilder atomBuilder) throws DataStorageException {

        if (atomBuilder.isData())
            return persistData(tmpGUID, atomBuilder, atomBuilder.getData());
        else if (atomBuilder.isLocation())
            return persistDataByLocation(tmpGUID, atomBuilder);
        else
            throw new DataStorageException("AtomBuilder not set correctly");
    }

    private StoredAtomInfo persistDataByLocation(IGUID guid, AtomBuilder atomBuilder) throws DataStorageException {

        try (Data data = atomBuilder.getData()) {

            return persistData(guid, atomBuilder, data);

        } catch (Exception e) {
            throw new DataStorageException(e);
        }
    }

    private StoredAtomInfo persistData(IGUID tmpGUID, AtomBuilder atomBuilder, Data data) throws DataStorageException {

        try {
            StoredAtomInfo storedAtomInfo = new StoredAtomInfo();

            if (atomBuilder.getRole() != null) {
                Pair<Data, String> encryptionResult = encrypt(data, atomBuilder.getRole());
                data = encryptionResult.X();

                storedAtomInfo.setRole(atomBuilder.getRole().guid());
                storedAtomInfo.setEncryptedKey(encryptionResult.Y());
            }

            IDirectory dataDirectory = localStorage.getDataDirectory();
            IFile file = localStorage.createFile(dataDirectory, tmpGUID.toMultiHash(), data);
            file.persist();

            return storedAtomInfo;

        } catch (PersistenceException | ProtectionException e) {
            throw new DataStorageException(e);
        }
    }

    public Pair<Data, String> encrypt(Data originalData, Role role) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SecretKey key = SymmetricEncryption.generateRandomKey();

            SymmetricEncryption.encrypt(key, originalData.getInputStream(), out);
            InputStream encryptedData = IO.OutputStreamToInputStream(out);

            String encryptedKey = role.encrypt(key);

            return new Pair<>(new InputStreamData(encryptedData), encryptedKey);
        } catch (CryptoException | ProtectionException | IOException e) {
            throw new ProtectionException(e);
        }

    }

    private IGUID generateGUID(Data data) throws GUIDGenerationException, IOException {

        return GUIDFactory.generateGUID(ALGORITHM.SHA256, data.getInputStream());
    }

    private IGUID generateGUID(Location location) {

        try (Data data = LocationUtility.getDataFromLocation(location)) {

            return generateGUID(data);

        } catch (Exception e) {
            return new InvalidID();
        }
    }

    private IFile atomFileInLocalStorage(IGUID guid) throws DataStorageException {
        IDirectory dataDirectory = localStorage.getDataDirectory();
        return localStorage.createFile(dataDirectory, guid.toMultiHash());
    }

    private Location makeLocalSOSLocation(IGUID guid) throws SourceLocationException {

        try {
            return new SOSLocation(localNodeGUID, guid);
        } catch (MalformedURLException e) {
            throw new SourceLocationException("SOSLocation could not be generated for entity: " + guid.toMultiHash(), e);
        }

    }

}
