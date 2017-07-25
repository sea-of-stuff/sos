package uk.ac.standrews.cs.sos.instrument.impl;

import uk.ac.standrews.cs.sos.instrument.Instrument;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DummyInstrument implements Instrument {

    @Override
    public void measure(String message) {}

    @Override
    public void measure(StatsTYPE statsTYPE, String message) {}
}
