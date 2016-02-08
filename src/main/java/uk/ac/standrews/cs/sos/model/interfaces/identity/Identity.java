package uk.ac.standrews.cs.sos.model.interfaces.identity;

import uk.ac.standrews.cs.sos.exceptions.identity.DecryptionException;
import uk.ac.standrews.cs.sos.exceptions.identity.EncryptionException;

import java.security.Key;

/**
 * Represents an identity of a client within the Sea of Stuff.
 *
 * TODO - what is an identity formed of? how to ensure that it is unique?
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Identity {

    byte[] sign(String text) throws EncryptionException ;
    boolean verify(String text, byte[] signature) throws DecryptionException ;

    Key getPublicKey();

}