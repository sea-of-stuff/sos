package uk.ac.standrews.cs.sos.storage.implementations.NetworkBased;

import uk.ac.standrews.cs.sos.storage.implementations.FileBased.FileBasedFile;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSFile;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedFile extends FileBasedFile implements SOSFile {

    public NetworkBasedFile(SOSDirectory parent, String name) {
        super(parent, name);
    }
}
