package uk.ac.standrews.cs.sos.storage.interfaces;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    SOSDirectory getRoot();

    SOSDirectory getDataDirectory();

    SOSDirectory getManifestDirectory();

    SOSDirectory getTestDirectory();

    SOSDirectory createDirectory(SOSDirectory parent, String name);
    SOSDirectory createDirectory(String name);

    SOSFile createFile(SOSDirectory parent, String filename);
}
