package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsDirectoryException;
import uk.ac.standrews.cs.storage.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.storage.interfaces.Directory;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;

/**
 * Helper for file system operations
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileHelper {

    public static void DeleteFile(File file) throws ManifestsDirectoryException {
        Directory parent = file.getParent();
        try {
            parent.remove(file.getName());
        } catch (BindingAbsentException e) {
            throw new ManifestsDirectoryException("Unable to delete file " + file.getName(), e);
        }
    }

    public static void RenameFile(String oldPathname, String newPathname) {
        java.io.File oldfile = new java.io.File(oldPathname);
        java.io.File newfile = new java.io.File(newPathname);
        oldfile.renameTo(newfile);
    }

    public static void RenameFile(File oldFile, File newFile) {
        try {
            java.io.File oldfile =  oldFile.toFile();
            java.io.File newfile = newFile.toFile();
            oldfile.renameTo(newfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void MakePath(String path) {
        java.io.File file = new java.io.File(path);
        java.io.File parent = file.getParentFile();
        if (parent != null)
            parent.mkdirs();
    }

    public static java.io.File MakeFile(java.io.File file) throws IOException {

        MakePath(file.getPath());
        file.createNewFile();

        return file;
    }
}
