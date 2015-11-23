package interfaces.components.manifests;

import interfaces.components.GUID;
import interfaces.components.identity.Signature;

/**
 * TODO
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Manifest {

    /**
     *
     * @return
     */
    GUID getGUID();

    /**
     *
     * @return
     */
    Signature getSignature();

    /**
     *
     * @return
     */
    long getTimestamp();
}
