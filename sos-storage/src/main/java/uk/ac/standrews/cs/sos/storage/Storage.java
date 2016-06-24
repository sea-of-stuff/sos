package uk.ac.standrews.cs.sos.storage;

import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    SOSDirectory getRoot();

    SOSDirectory getDataDirectory();

    SOSDirectory getManifestDirectory();

    SOSDirectory getTestDirectory();
}
