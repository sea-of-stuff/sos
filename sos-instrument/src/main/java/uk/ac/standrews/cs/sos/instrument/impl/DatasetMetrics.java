package uk.ac.standrews.cs.sos.instrument.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import uk.ac.standrews.cs.sos.instrument.Metrics;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

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
public class DatasetMetrics implements Metrics {

    private String directory;
    private int numberOfDirectories;
    private ArrayList<Long> filesSize;
    private long totalSize;
    private Set<String> fileExtensions;

    private static ArrayList<FSEntry> entries;

    private DatasetMetrics() {
        entries = new ArrayList<>();
    }

    public static DatasetMetrics measure(File directory) throws IOException {

        DatasetMetrics metrics = new DatasetMetrics();

        DatasetFileVisitor<Path> fv = new DatasetFileVisitor<>();
        Files.walkFileTree(directory.toPath(), fv);

        metrics.setDirectory(directory.getAbsolutePath());
        metrics.setFilesSize(fv.filesSize);
        metrics.setNumberOfDirectories(fv.numberOfDirectories);
        metrics.setTotalSize(fv.totalSize);
        metrics.setFileExtensions(fv.fileExtensions);

        return metrics;
    }

    @Override
    public String toString() {

        String retval = String.format(
                "%nDataset Metrics for directory: %s%n" +
                "Number of files: %d%n" +
                "Number of directories: %d%n" +
                "Total size in bytes: %d%n" +
                "Largest file in bytes: %d%n" +
                "Smallest file in bytes: %d%n" +
                "Range: %d%n" +
                "Mean size in bytes: %f%n" +
                "Median: %f%n" +
                "Mode: %d%n" +
                "Variance: %d%n" +
                "STD Dev: %f%n" +
                "Coefficient of Variation: %f%n" +
                "*Pearson's first skewness coefficient: %f%n" +
                "Pearson's second skewness coefficient: %f%n" +
                "File Extensions: %s%n",
                getDirectory(),
                getNumberOfFiles(), getNumberOfDirectories(), getTotalSize(),
                getLargestFileSize(), getSmallestFileSize(), getFileSizeRange(),
                getMean(), getMedian(), getMode(), getVariance(), getSTDDev(),
                getCoefficientOfVariation(), getPearsonFirstSkewness(), getPearsonSecondSkewness(),
                StringUtils.join(getFileExtensions(), ", "));
        return retval;
    }

    // FIXME - not considering folders?
    @Override
    public String tsvHeader() {
        return "Type" + TAB + "Name" + TAB + "Size" + TAB + "Filetype";
    }

    @Override
    public String tsv() {

        StringBuilder retval = new StringBuilder();
        for(FSEntry entry:entries) {
            retval.append(entry.type).append(TAB).append(entry.name).append(TAB).append(entry.size).append(TAB).append(entry.filetype);
            retval.append("\n");
        }

        return retval.toString();
    }

    @Override
    public void setStatsType(StatsTYPE statsType) {
        // NOTHING
    }

    @Override
    public void setSubType(StatsTYPE subtype) {
        // NOTHING
    }

    public double getMean() {
        return totalSize/ (double) getNumberOfFiles();
    }

    public double getMedian() {
        return median(filesSize);
    }

    public long getMode() {
        return mode(filesSize);
    }

    public long getVariance() {
        long tmp = 0;
        for(Long fileSize:filesSize) {
            tmp += Math.pow(fileSize - getMean(), 2);
        }

        return tmp / filesSize.size();
    }

    public double getSTDDev() {
        return Math.sqrt(getVariance());
    }

    // https://en.wikipedia.org/wiki/Coefficient_of_variation
    public double getCoefficientOfVariation() {
        return getSTDDev() / getMean();
    }

    public int getNumberOfFiles() {
        return filesSize.size();
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public double getPearsonFirstSkewness() {
        return (getMean() - getMode()) / getSTDDev();
    }

    public double getPearsonSecondSkewness() {
        return (3*(getMean() - getMedian())) / getSTDDev();
    }

    public long getLargestFileSize() {
        return Collections.max(filesSize);
    }

    public long getSmallestFileSize() {
        return Collections.min(filesSize);
    }

    public long getFileSizeRange() {
        return getLargestFileSize() - getSmallestFileSize();
    }

    public int getNumberOfDirectories() {
        return numberOfDirectories;
    }

    public void setNumberOfDirectories(int numberOfDirectories) {
        this.numberOfDirectories = numberOfDirectories;
    }

    public Set<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(Set<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public ArrayList<Long> getFilesSize() {
        return filesSize;
    }

    public void setFilesSize(ArrayList<Long> filesSize) {
        this.filesSize = filesSize;
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
    private static long mode(ArrayList<Long> a) {
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

    private static List<Integer> getModes(final List<Integer> numbers) {
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

    private static class DatasetFileVisitor<T> extends SimpleFileVisitor<Path> {

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

            entries.add(new FSEntry("file", file.getFileName().toString(), file.toFile().length(), extension));

            return FileVisitResult.CONTINUE;
        }
    }

    private static class FSEntry {

        public String type;
        public String name;
        public long size;
        public String filetype;

        public FSEntry(String type, String name, long size, String filetype) {
            this.type = type;
            this.name = name;
            this.size = size;
            this.filetype = filetype;
        }
    }
}
