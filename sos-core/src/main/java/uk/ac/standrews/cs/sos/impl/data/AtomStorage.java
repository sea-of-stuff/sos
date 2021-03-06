/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.data;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.exceptions.RenameException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.constants.Internals;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.location.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.LocationUtility;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleType;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
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

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/*
 * This is the class that will take care of storing atom's data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomStorage {

    private final IGUID localNodeGUID;
    private final LocalStorage localStorage;

    public AtomStorage(IGUID localNodeGUID, LocalStorage storage) {
        this.localNodeGUID = localNodeGUID;
        this.localStorage = storage;
    }

    public StoredAtomInfo store(AtomBuilder atomBuilder, BundleType type) throws DataStorageException {

        try {
            StoredAtomInfo storedAtomInfo = storeToLocalStorage(atomBuilder);
            Location localLocation = makeLocalSOSLocation(storedAtomInfo.getGuid());
            LocationBundle bundle = new LocationBundle(type, localLocation);

            return storedAtomInfo.setLocationBundle(bundle);

        } catch (SourceLocationException e) {
            throw new DataStorageException("Unable to store data properly");
        }

    }

    public Data decryptData(Data encryptedData, SecretKey decryptedKey) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SymmetricEncryption.decrypt(decryptedKey, encryptedData.getInputStream(), out);
            try (InputStream inputStream = IO.OutputStreamToInputStream(out)) {
                return new InputStreamData(inputStream);
            }

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
            IGUID tmpGUID = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
            StoredAtomInfo storedAtomInfo = persistData(tmpGUID, atomBuilder);

            IFile tmpCachedLocation = createAtomFileInLocalStorage(tmpGUID);
            Location location = new URILocation(tmpCachedLocation.getPathname());
            IGUID guid = generateGUID(location);
            tmpCachedLocation.rename(guid.toMultiHash());

            return storedAtomInfo.setGuid(guid);

        } catch (RenameException | URISyntaxException e) {
            throw new DataStorageException("Unable to store data properly");
        }

    }

    private StoredAtomInfo persistData(IGUID tmpGUID, AtomBuilder atomBuilder) throws DataStorageException {

        try (Data data = atomBuilder.getData()) {
            return persistData(tmpGUID, atomBuilder, data);
        } catch (IOException e) {
            throw new DataStorageException("Data source could not be closed");
        }
    }

    private StoredAtomInfo persistData(IGUID tmpGUID, AtomBuilder atomBuilder, Data data) throws DataStorageException {

        try {
            StoredAtomInfo storedAtomInfo = new StoredAtomInfo();

            if (!atomBuilder.isAlreadyProtected() && atomBuilder.isProtect()) {

                if (atomBuilder.getRole() == null) throw new DataStorageException("No role provided for protecting data");

                Pair<Data, String> encryptionResult = encrypt(data, atomBuilder.getRole());
                data = encryptionResult.X();

                storedAtomInfo.setRole(atomBuilder.getRole().guid());
                storedAtomInfo.setEncryptedKey(encryptionResult.Y());
            }

            IDirectory dataDirectory = localStorage.getAtomsDirectory();
            IFile file = localStorage.createFile(dataDirectory, tmpGUID.toMultiHash(), data);
            file.persist();

            return storedAtomInfo;

        } catch (PersistenceException | ProtectionException e) {
            throw new DataStorageException(e);
        }
    }

    private Pair<Data, String> encrypt(Data originalData, Role role) throws ProtectionException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SecretKey key = SymmetricEncryption.generateRandomKey();
            SymmetricEncryption.encrypt(key, originalData.getInputStream(), out);

            try (InputStream encryptedData = IO.OutputStreamToInputStream(out)) {

                String encryptedKey = role.encrypt(key);
                return new Pair<>(new InputStreamData(encryptedData), encryptedKey);
            }

        } catch (CryptoException | ProtectionException | IOException e) {
            throw new ProtectionException(e);
        }

    }

    private IGUID generateGUID(Location location) {

        try (Data data = LocationUtility.getData(location)) {

            long start = System.nanoTime();
            IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, data.getInputStream());
            long duration = System.nanoTime() - start;

            StatsTYPE subtype = StatsTYPE.getHashType(Internals.GUID_ALGORITHM);
            InstrumentFactory.instance().measure(StatsTYPE.guid_data, subtype, Long.toString(data.getSize()), duration);

            return guid;

        } catch (IOException | GUIDGenerationException e) {
            return new InvalidID();
        }
    }

    private IFile createAtomFileInLocalStorage(IGUID guid) throws DataStorageException {
        IDirectory dataDirectory = localStorage.getAtomsDirectory();
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
