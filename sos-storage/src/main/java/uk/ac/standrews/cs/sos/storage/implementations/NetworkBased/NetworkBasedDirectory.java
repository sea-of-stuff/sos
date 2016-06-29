package uk.ac.standrews.cs.sos.storage.implementations.NetworkBased;

import uk.ac.standrews.cs.sos.storage.implementations.FileBased.FileBasedDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedDirectory extends FileBasedDirectory {

    public NetworkBasedDirectory(SOSDirectory parent, String name) {
        super(parent, name);
    }
}
