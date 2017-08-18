package uk.ac.standrews.cs.sos.experiments;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Metrics {

    public static void Dataset(File directory) {

        DatasetFileVisitor<Path> fv = new DatasetFileVisitor<>();

        try {
            Files.walkFileTree(directory.toPath(), fv);

            System.out.println("");
            System.out.println("==============");

            long mean = fv.totalSize / fv.numberOfFiles;
            double median = median(fv.filesSize);
            long mode = mode(fv.filesSize);

            System.out.println("Number of files: " + fv.numberOfFiles + " for a total size in bytes of: " + fv.totalSize);
            System.out.println("Average file size: " + mean + " bytes or " + mean / 1000.0 + " kb");
            System.out.println("Median: " + median);
            System.out.println("Mode: " + mode);

            long variance = 0;
            for(Long fileSize:fv.filesSize) {
                variance += Math.pow(fileSize - mean, 2);
            }
            variance = variance / fv.numberOfFiles;
            double stdDev = Math.sqrt(variance);
            System.out.println("Variance: " + variance);
            System.out.println("STD Dev: " + stdDev);
            double coefficientOfVariation = stdDev / mean; // https://en.wikipedia.org/wiki/Coefficient_of_variation
            System.out.println("Coefficient of Variation: " + coefficientOfVariation);


            // Pearson's first skewness coefficient
            double pearsonFirstSkewness = (mean - mode) / stdDev;
            System.out.println("*Pearson's first skewness coefficient: " + pearsonFirstSkewness);

            // Pearson's second skewness coefficient
            double pearsonSecondSkewness = (3*(mean - median)) / stdDev;
            System.out.println("Pearson's second skewness coefficient: " + pearsonSecondSkewness);

            System.out.println("Largest file: " + Collections.max(fv.filesSize));
            System.out.println("Smallest file: " + Collections.min(fv.filesSize));
            System.out.println("Range: " + (Collections.max(fv.filesSize) - Collections.min(fv.filesSize)));
            System.out.println("Number of directories: " + fv.numberOfDirectories);
            System.out.print("Extensions: ");
            for(String extension:fv.fileExtensions) {
                System.out.print(extension + ", ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double median(ArrayList<Long> arr) {

        Collections.sort(arr);
        int length = arr.size();
        double median;
        if (length % 2 == 0)
            median = ((double)arr.get(length/2) + (double)arr.get(length/2 - 1))/2;
        else
            median = (double) arr.get(length/2);

        return median;
    }

    // Assuming only one mode
    public static long mode(ArrayList<Long> a) {
        long maxValue = 0, maxCount = 0;

        for (int i = 0; i < a.size(); ++i) {
            int count = 0;
            for (int j = 0; j < a.size(); ++j) {
                if (a.get(j) == a.get(i)) ++count;
            }
            if (count > maxCount) {
                maxCount = count;
                maxValue = a.get(i);
            }
        }

        return maxValue;
    }

    public static List<Integer> getModes(final List<Integer> numbers) {
        final Map<Integer, Long> countFrequencies = numbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final long maxFrequency = countFrequencies.values().stream()
                .mapToLong(count -> count)
                .max().orElse(-1);

        return countFrequencies.entrySet().stream()
                .filter(tuple -> tuple.getValue() == maxFrequency)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
            //System.out.println(file);

            numberOfFiles++;
            totalSize += file.toFile().length();
            filesSize.add(file.toFile().length());

            String extension = FilenameUtils.getExtension(file.getFileName().toString());
            fileExtensions.add(extension);

            return FileVisitResult.CONTINUE;
        }
    }
}
