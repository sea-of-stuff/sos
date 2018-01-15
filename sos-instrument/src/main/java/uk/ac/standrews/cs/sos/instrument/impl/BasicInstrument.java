package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Instrument;
import uk.ac.standrews.cs.sos.instrument.Metrics;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

import static uk.ac.standrews.cs.sos.instrument.Metrics.TAB;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicInstrument implements Instrument {

    public static final String DATASET_SUMMARY = "_dataset.txt";
    public static final String DATASET_FILES = "_dataset_files.tsv";

    private Statistics statistics;
    private String filename;

    private static final Object LOCK_MEASUREMENTS_QUEUE = new Object();
    private Queue<Metrics> measurementsQueue;

    public BasicInstrument(Statistics statistics, String filename) throws IOException {
        this.statistics = statistics;
        this.filename = filename;

        this.measurementsQueue = new LinkedList<>();

        boolean fileIsEmpty = fileIsEmpty(filename);
        if (fileIsEmpty) {
            try (FileWriter fileWriter = new FileWriter(new File(filename + ".tsv"), true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write("StatsTYPE" + TAB + "Subtype" + TAB);
                writeHeader(bufferedWriter, new AppMetrics(), true);
            }
        }

        System.out.println("Instrumentation output will be collected at the file: " + filename + ".tsv");
        System.out.println("Statistics about the dataset used will be available at the file: " + (filename + DATASET_SUMMARY));
        System.out.println("Statistics about the dataset (per file) will be available at the file: " + (filename + DATASET_FILES));
    }

    @Override
    public void measureDataset(File directory) throws IOException {

        try (FileWriter fileWriter = new FileWriter(new File(filename + DATASET_SUMMARY), true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            DatasetMetrics datasetMetrics = DatasetMetrics.measure(directory);
            bufferedWriter.write(datasetMetrics.toString());
        }

        try (FileWriter fileWriter = new FileWriter(new File(filename + DATASET_FILES), true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            DatasetMetrics datasetMetrics = DatasetMetrics.measure(directory);
            bufferedWriter.write(datasetMetrics.tsvHeader());
            bufferedWriter.newLine();
            bufferedWriter.write(datasetMetrics.tsv());
        }
    }

    @Override
    public void measure(String message) {

        measure(StatsTYPE.any, StatsTYPE.none, message);
    }

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message) {
        measure(statsTYPE, subtype, message, -1);
    }

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure) {

        if (statistics.isEnabled(statsTYPE)) {

            synchronized (LOCK_MEASUREMENTS_QUEUE) {
                AppMetrics appMeasure = AppMetrics.measure(message);
                appMeasure.setUserMeasure(measure);
                appMeasure.setStatsType(statsTYPE);
                appMeasure.setSubType(subtype);

                measurementsQueue.add(appMeasure);
            }
        }
    }

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure, long measure_2) {

        if (statistics.isEnabled(statsTYPE)) {

            synchronized (LOCK_MEASUREMENTS_QUEUE) {
                AppMetrics appMeasure = AppMetrics.measure(message);
                appMeasure.setUserMeasure(measure);
                appMeasure.setUserMeasure_2(measure_2);
                appMeasure.setStatsType(statsTYPE);
                appMeasure.setSubType(subtype);

                measurementsQueue.add(appMeasure);
            }
        }
    }

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure, long measure_2, long measure_3) {

        if (statistics.isEnabled(statsTYPE)) {

            synchronized (LOCK_MEASUREMENTS_QUEUE) {
                AppMetrics appMeasure = AppMetrics.measure(message);
                appMeasure.setUserMeasure(measure);
                appMeasure.setUserMeasure_2(measure_2);
                appMeasure.setUserMeasure_3(measure_3);
                appMeasure.setStatsType(statsTYPE);
                appMeasure.setSubType(subtype);

                measurementsQueue.add(appMeasure);
            }
        }
    }

    @Override
    public void flush() {

        synchronized (LOCK_MEASUREMENTS_QUEUE) {

            try (FileWriter fileWriter = new FileWriter(new File(filename + ".tsv"), true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                for (Metrics metrics : measurementsQueue) {
                    write(bufferedWriter, metrics, true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            measurementsQueue.clear();

        }
    }

    private void write(BufferedWriter bufferedWriter, Metrics metrics, boolean last) throws IOException {

        bufferedWriter.write(metrics.tsv());
        if (!last) bufferedWriter.write(TAB);

        if (last) bufferedWriter.newLine();
    }

    private void writeHeader(BufferedWriter bufferedWriter, Metrics metrics, boolean last) throws IOException {

        bufferedWriter.write(metrics.tsvHeader());
        if (!last) bufferedWriter.write(TAB);

        if (last) bufferedWriter.newLine();
    }

    private boolean fileIsEmpty(String filename) throws IOException {

        if (!new File(filename).exists()) return true;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            if (br.readLine() == null) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws IOException {

        Instrument instrument = new BasicInstrument(new Statistics(), "TEST");
        instrument.measure("test one");
        instrument.flush();
        instrument.measure("test two");
        instrument.measure("test three");
        instrument.flush();
    }

}
