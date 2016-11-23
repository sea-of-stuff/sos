package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

/**
 * This interface establishes the link between the FS objects and the SOS versioning feature
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
interface SOSVersionableObject {

    /**
     *
     * @return version for this object
     */
    Asset getAsset();

}
