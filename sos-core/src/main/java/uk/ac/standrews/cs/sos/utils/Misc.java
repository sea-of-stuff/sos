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
