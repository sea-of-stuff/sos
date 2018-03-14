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
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.json.UserDeserializer;
import uk.ac.standrews.cs.sos.impl.json.UserSerializer;

import java.security.PublicKey;

/**
 * Used to create roles
 * A user has one or more roles
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@JsonSerialize(using = UserSerializer.class)
@JsonDeserialize(using = UserDeserializer.class)
public interface User extends Manifest {

    /**
     * Unique GUID for the user
     *
     * @return guid of the user
     */
    IGUID guid();

    /**
     * Human-readable name for the user
     * e.g. Simone Ivan Conte
     *
     * @return name of the user
     */
    String getName();

    /**
     * Public key of the user used to generate signatures
     *
     * @return public key of the user
     */
    PublicKey getSignatureCertificate();

    /**
     * Sign some given text using this user private key
     *
     * @param text to be signed
     * @return the signed text
     * @throws SignatureException if the text could not be signed
     */
    String sign(String text) throws SignatureException;

    /**
     * Verify that the given text and signature match
     *
     * @param text
     * @param signatureToVerify
     * @return
     * @throws SignatureException
     */
    boolean verify(String text, String signatureToVerify) throws SignatureException;

    /**
     * Return true if this User object has private keys
     *
     * @return true if private keys are known
     */
    boolean hasPrivateKeys();

}
