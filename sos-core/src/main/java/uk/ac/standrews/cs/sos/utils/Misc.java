package uk.ac.standrews.cs.sos.utils;

import java.util.Random;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Misc {

    // http://stackoverflow.com/a/5439547/2467938
    public static boolean isNumber(String s) {
        return isNumber(s,10);
    }

    private static boolean isNumber(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
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

}
