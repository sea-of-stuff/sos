/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module instrument.
 *
 * instrument is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * instrument is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with instrument. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

    public static Instrument instance(Statistics statistics, String filename) throws IOException {

        if (basicInstrument == null) {
            System.out.println("---------------------------------------------");
            basicInstrument = new BasicInstrument(statistics, filename);
//            backgroundInstrument = new BackgroundInstrument(filename);
            System.out.println("---------------------------------------------");
        }

        return basicInstrument;
    }

    public static void start() {

//        if (backgroundInstrument != null) {
//            backgroundInstrument.start();
//        }

    }

    public static void flush() {
        if (basicInstrument != null) {
            basicInstrument.flush();
        }

//        if (backgroundInstrument != null) {
//            backgroundInstrument.flush();
//        }
    }

    public static void stop() {

//        if (backgroundInstrument != null) {
//            backgroundInstrument.stop();
//        }
    }
}
