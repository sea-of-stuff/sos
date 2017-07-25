package uk.ac.standrews.cs.sos.instrument;

import java.io.*;

/**
 * TODO - implement singleton pattern
 * TODO - print stats in json or csv format
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Instrument {

    private MeasureTYPE measureTYPE;
    private String filename;

    private static Instrument instance;

    public Instrument(MeasureTYPE measureTYPE, String filename) throws IOException {
        this.measureTYPE = measureTYPE;
        this.filename = filename;

        boolean fileIsEmpty = fileIsEmpty(filename);
        if (fileIsEmpty) {
            try (FileWriter fileWriter = new FileWriter(new File(filename), true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                writeHeader(bufferedWriter, new AppMeasure(), false);
                writeHeader(bufferedWriter, new OSMeasures(), true);
            }
        }
    }

    public static Instrument instance() {

        return instance;
    }

    public static Instrument instance(MeasureTYPE measureTYPE, String filename) throws IOException {

        if (instance == null) {
            instance = new Instrument(measureTYPE, filename);
        }

        return instance;
    }

    public void measure(String message) {

        try (FileWriter fileWriter = new FileWriter(new File(filename), true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            AppMeasure appMeasure = AppMeasure.measure(message);
            write(bufferedWriter, appMeasure, false);

            OSMeasures osMeasures = OSMeasures.measure();
            write(bufferedWriter, osMeasures, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO - measure other things...network?
    }

    private void write(BufferedWriter bufferedWriter, Measure measure, boolean last) throws IOException {

        switch (measureTYPE) {
            case STRING:
                bufferedWriter.write(measure.toString());
                bufferedWriter.newLine();
                break;
            case CSV:
                bufferedWriter.write(measure.csv());
                if (!last) bufferedWriter.write(Measure.COMMA);
                break;
        }

        if (last) bufferedWriter.newLine();
    }

    private void writeHeader(BufferedWriter bufferedWriter, Measure measure, boolean last) throws IOException {

        switch (measureTYPE) {
            case STRING:
                break;
            case CSV:
                bufferedWriter.write(measure.csvHeader());
                if (!last) bufferedWriter.write(Measure.COMMA);
                break;
        }

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

        new Instrument(MeasureTYPE.CSV, "TEST.json").measure("test message");
    }


}
