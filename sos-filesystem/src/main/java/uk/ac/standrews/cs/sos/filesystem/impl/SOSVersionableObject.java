package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.sos.interfaces.manifests.Version;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
interface SOSVersionableObject {

    /**
     *
     * @return version for this object
     */
    Version getVersion();

}
