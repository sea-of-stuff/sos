package model.interfaces.identity;

import model.exceptions.DecryptionException;
import model.exceptions.EncryptionException;

import java.nio.file.Path;
import java.security.Key;

/**
 * Represents an identity of a client within the Sea of Stuff.
 *
 * TODO - what is an identity formed of? how to ensure that it is unique?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Identity {

    byte[] encrypt(String text) throws EncryptionException;
    String decrypt(byte[] text) throws DecryptionException;

    /**
     * Load identity - TODO - path to keys?
     * @param path
     */
    void loadIdentity(Path path);

    Key getPublicKey();

}
