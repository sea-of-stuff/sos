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
package uk.ac.standrews.cs.sos.utils;

import java.util.Random;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Misc {

    public static boolean isIntegerNumber(String s) {

        try {
            long v = Long.parseLong(s);
            return true;
        } catch (NumberFormatException ignored) {}

        return false;
    }

    public static boolean isRealNumber(String s) {

        try {
            double v = Double.parseDouble(s);
            return true;
        } catch (NumberFormatException ignored) {}

        return false;
    }

    public static boolean isBoolean(String s) {

        return s.toLowerCase().equals("true") || s.toLowerCase().equals("false");
    }

    // https://stackoverflow.com/a/1520212/2467938
    // Implementing Fisher–Yates shuffle
    public static void shuffleArray(Object[] ar) {

        Random random = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Simple swap
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static double toKB(long bytes) {
        return bytes / 1024.0;
    }

    public static double toMB(long bytes) {
        return toKB(bytes) / 1024.0;
    }

    // https://codereview.stackexchange.com/a/26698/17102
    private static long gcd(long p, long q) {
        if (q == 0) return p;
        else return gcd(q, p % q);
    }

    // https://codereview.stackexchange.com/a/26698/17102
    public static String ratio(long a, long b) {
        final long gcd = gcd(a,b);
        return String.format("%d:%d\t(%.2f)", a/gcd, b/gcd, (a/(b*1.0)));
    }
}
