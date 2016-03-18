package uk.ac.standrews.cs.sos.model.implementations.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FileHelper {

    public static void deleteFile(final String path) {
        File file = new File(path);
        file.delete();
    }

    public static void copyToFile(InputStream inputStream, String destination) throws IOException {
        Files.copy(inputStream, locationToPath(destination));
    }

    private static Path locationToPath(String path) {
        return new File(path).toPath();
    }

    public static void touchDir(String path) {
        File parent = new File(path).getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            parent.mkdirs();
        }
    }

    public static boolean pathExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void renameFile(String oldPathname, String newPathname) {
        File oldfile =new File(oldPathname);
        File newfile =new File(newPathname);
        oldfile.renameTo(newfile);
    }
}
