package uk.ac.standrews.cs.sos.storage.implementations.network;

import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedDirectory;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedDirectory extends FileBasedDirectory {

    public NetworkBasedDirectory(Directory parent, String name) {
        super(parent, name);
    }
}
