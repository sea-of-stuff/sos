package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestManagerException;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileHelper {

    public static void DeleteFile(File file) throws ManifestManagerException {
        Directory parent = file.getParent();
        try {
            parent.remove(file.getName());
        } catch (BindingAbsentException e) {
            throw new ManifestManagerException("Unable to delete file " + file.getName(), e);
        }
    }

    public static void RenameFile(String oldPathname, String newPathname) {
        java.io.File oldfile =new java.io.File(oldPathname);
        java.io.File newfile =new java.io.File(newPathname);
        oldfile.renameTo(newfile);
    }
}
