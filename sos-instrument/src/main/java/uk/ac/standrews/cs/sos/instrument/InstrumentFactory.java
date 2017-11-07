package uk.ac.standrews.cs.sos.instrument;

import uk.ac.standrews.cs.sos.instrument.impl.BackgroundInstrument;
import uk.ac.standrews.cs.sos.instrument.impl.BasicInstrument;
import uk.ac.standrews.cs.sos.instrument.impl.DummyInstrument;
import uk.ac.standrews.cs.sos.instrument.impl.Statistics;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class InstrumentFactory {

    private static BasicInstrument basicInstrument;
    private static BackgroundInstrument backgroundInstrument;

    public static Instrument instance() {

        if (basicInstrument == null) {
            return new DummyInstrument();
        }

        return basicInstrument;
    }

    public static Instrument instance(Statistics statistics, OutputTYPE outputTYPE, String filename) throws IOException {

        if (basicInstrument == null) {
            basicInstrument = new BasicInstrument(statistics, outputTYPE, filename);
            backgroundInstrument = new BackgroundInstrument(filename);
        }

        return basicInstrument;
    }

    public static void start() {

        if (backgroundInstrument != null) {
            backgroundInstrument.start();
        }

    }

    public static void flush() {
        if (basicInstrument != null) {
            basicInstrument.flush();
        }

        if (backgroundInstrument != null) {
            backgroundInstrument.flush();
        }
    }

    public static void stop() {

        if (backgroundInstrument != null) {
            backgroundInstrument.stop();
        }
    }
}
