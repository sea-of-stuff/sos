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
package uk.ac.standrews.cs.sos.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.impl.json.RoleDeserializer;
import uk.ac.standrews.cs.sos.impl.json.RoleSerializer;

import javax.crypto.SecretKey;
import java.security.PublicKey;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = RoleSerializer.class)
@JsonDeserialize(using = RoleDeserializer.class)
public interface Role extends User {

    /**
     * GUID for this role
     *
     * @return guid of this role
     */
    IGUID guid();

    /**
     * Get the GUID for the user that created this Role
     * e.g. guid for user Simone
     *
     * @return guid of the user
     */
    IGUID getUser();

    /**
     * Get the name of the role
     * e.g. Simone's work
     *
     * @return name for the role
     */
    String getName();

    /**
     * Used to sign metadata, manifests, etc
     *
     * @return certificate for this role
     */
    PublicKey getSignaturePublicKey();

    /**
     * Used to encrypt symmetric keys
     * This is an asymmetric key, such as RSA
     *
     * @return key to sign keys
     */
    PublicKey getPubKey();

    /**
     * Signature for this role manifest.
     * This signature is generated using the User public key.
     *
     * @return signfature for this role
     */
    String getSignature();

    /**
     * Encrypt a symmetric key using an asymmetric key
     *
     * @param key (symmetric) to encrypt
     * @return encrypted key
     * @throws ProtectionException if unable to encrpyt the key
     */
    String encrypt(SecretKey key) throws ProtectionException;

    /**
     * Encrypted key is decripted using the private key (e.g. RSA)
     *
     * @param encryptedKey key to decrypt
     * @return decrypted key
     * @throws ProtectionException if unable to decrypt the key
     */
    SecretKey decrypt(String encryptedKey) throws ProtectionException;

}
