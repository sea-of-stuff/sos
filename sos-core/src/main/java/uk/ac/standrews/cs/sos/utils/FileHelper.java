package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileHelper {

    public static void deleteFile(final String path) {
        java.io.File file = new java.io.File(path);
        file.delete();
    }

    public static void copyToFile(InputStream inputStream, String destination) throws IOException {
        Files.copy(inputStream, locationToPath(destination));
    }

    private static Path locationToPath(String path) {
        return new java.io.File(path).toPath();
    }


    public static void touchDir(File file) {
        touchDir(file.getPathname());
    }

    public static void touchDir(String path) {
        java.io.File parent = new java.io.File(path).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    public static boolean pathExists(String path) {
        java.io.File file = new java.io.File(path);
        return file.exists();
    }

    public static void renameFile(String oldPathname, String newPathname) {
        java.io.File oldfile =new java.io.File(oldPathname);
        java.io.File newfile =new java.io.File(newPathname);
        oldfile.renameTo(newfile);
    }
}
