package uk.ac.standrews.cs.sos.exceptions.crypto;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DecryptionException extends CryptoException {

    public DecryptionException(Throwable throwable) {
        super(throwable);
    }
}
