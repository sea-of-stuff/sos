package uk.ac.standrews.cs.sos.experiments;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Metrics {

    public static void Dataset(File directory) {

        DatasetFileVisitor<Path> fv = new DatasetFileVisitor<>();

        try {
            Files.walkFileTree(directory.toPath(), fv);

            System.out.println("==============");

            long averageFileSize = fv.totalSize / fv.numberOfFiles;

            System.out.println("Number of files: " + fv.numberOfFiles + " for a total size in bytes of: " + fv.totalSize);
            System.out.println("Average file size: " + averageFileSize + "bytes or " + averageFileSize / 1000.0 + " kb");

            long variance = 0;
            for(Long fileSize:fv.filesSize) {
                variance += Math.pow(fileSize - averageFileSize, 2);
            }
            variance = variance / fv.numberOfFiles;
            double stdDev = Math.sqrt(variance);
            System.out.println("Variance: " + variance);
            System.out.println("STD Dev: " + stdDev);

            System.out.println("Largest file: " + Collections.max(fv.filesSize));
            System.out.println("Smallest file: " + Collections.min(fv.filesSize));
            System.out.println("Number of directories: " + fv.numberOfDirectories);
            System.out.print("Extensions: ");
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
        ArrayList<Long> filesSize;

        DatasetFileVisitor() {
            fileExtensions = new LinkedHashSet<>();
            filesSize = new ArrayList<>();
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
            filesSize.add(file.toFile().length());

            String extension = FilenameUtils.getExtension(file.getFileName().toString());
            fileExtensions.add(extension);

            return FileVisitResult.CONTINUE;
        }
    }
}
