package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Instrument;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DummyInstrument implements Instrument {

    @Override
    public void measureDataset(File directory) throws IOException {}

    @Override
    public void measure(String message) {}

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message) {}

    @Override
    public void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure) {}

    @Override
    public void flush() {}
}
