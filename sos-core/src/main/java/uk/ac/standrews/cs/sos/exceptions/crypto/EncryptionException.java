package uk.ac.standrews.cs.sos.exceptions.crypto;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class EncryptionException extends CryptoException {

    public EncryptionException(Throwable throwable) {
        super(throwable);
    }
}
