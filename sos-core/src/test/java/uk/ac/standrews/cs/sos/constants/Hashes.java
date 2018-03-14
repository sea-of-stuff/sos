/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.constants;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Hashes {

    public static final String TEST_STRING = "TEST";
    // Hash generated using http://www.sha1-online.com/
    public static final String TEST_STRING_HASHED = "SHA256_16_94ee059335e587e501cc4bf90613e0814f00a7b08bc7c648fd865a2af6a22cc2";

    public static final String TEST_HTTP_BIN_CONTENT = "abcdefghij";
    public static final String TEST_HTTP_BIN_URL = "http://httpbin.org/range/10";
    public static final String TEST_HTTP_BIN_HASH = "SHA256_16_72399361da6a7754fec986dca5b7cbaf1c810a28ded4abaf56b2106d06cb78b0";

    public static final String TEST_HTTP_BIN_URL_OTHER = "http://httpbin.org/range/11";
}
