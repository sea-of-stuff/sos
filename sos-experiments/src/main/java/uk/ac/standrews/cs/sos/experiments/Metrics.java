package uk.ac.standrews.cs.sos.experiments;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Metrics {

    public static void Dataset(File directory) {

        DatasetFileVisitor<Path> fv = new DatasetFileVisitor();

        try {
            Files.walkFileTree(directory.toPath(), fv);

            System.out.println("number of files: " + fv.numberOfFiles + " size in bytes: " + fv.totalSize);
            System.out.println("average file size: " + fv.totalSize / fv.numberOfFiles + " in kb " + fv.totalSize / (fv.numberOfFiles * 1000.0));
            System.out.println("number of directories: " + fv.numberOfDirectories);
            System.out.println("extensions: ");
            for(String extension:fv.fileExtensions) {
                System.out.print(extension + ", ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class DatasetFileVisitor<T> extends SimpleFileVisitor<Path> {

        int numberOfFiles = 0;
        int totalSize = 0;
        int numberOfDirectories = -1; // Do not count the parent directory
        Set<String> fileExtensions;

        public DatasetFileVisitor() {
            fileExtensions = new LinkedHashSet<>();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            super.preVisitDirectory(dir, attrs);

            numberOfDirectories++;

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            System.out.println(file);

            numberOfFiles++;
            totalSize += file.toFile().length();

            String extension = FilenameUtils.getExtension(file.getFileName().toString());
            fileExtensions.add(extension);

            return FileVisitResult.CONTINUE;
        }
    }
}
