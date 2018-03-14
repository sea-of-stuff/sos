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

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Instrument {

    void measureDataset(File directory) throws IOException;

    void measure(String message);

    void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message);

    void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure);

    void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure, long measure_2);

    void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, long measure, long measure_2, long measure_3);

    void measure(StatsTYPE statsTYPE, StatsTYPE subtype, String message, String message_2, long measure, long measure_2);

    void flush();

}
