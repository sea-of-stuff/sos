package uk.ac.standrews.cs.sos.storage.implementations.network;

import uk.ac.standrews.cs.sos.storage.implementations.filesystem.FileBasedFile;
import uk.ac.standrews.cs.sos.storage.interfaces.Directory;
import uk.ac.standrews.cs.sos.storage.interfaces.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NetworkBasedFile extends FileBasedFile implements File {

    public NetworkBasedFile(Directory parent, String name) {
        super(parent, name);
    }
}
