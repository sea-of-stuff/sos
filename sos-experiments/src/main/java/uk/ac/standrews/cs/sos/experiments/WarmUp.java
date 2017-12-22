package uk.ac.standrews.cs.sos.experiments;

import java.security.SecureRandom;

/**
 * Code to warm up the JVM.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WarmUp {

    public static void main(String[] args) {

        run();
    }

    private static double global = 0;

    public static void run() {

        System.out.println("Starting JVM WarmUp.");
        long start = System.nanoTime();
        for (int i = 0; i < 10000000; i++) {
            Clazz clazz = new Clazz();
            double val = clazz.method(i);
            val = 1 - val;
            global = val;
        }
        System.out.println("JVM WarmUp finished in " + (System.nanoTime() - start) / 1000000000.0 + " seconds.");
    }

    private static class Clazz {

        double method(int a) {

            global += a * new SecureRandom().nextDouble();
            global += 3;
            global /= 2.0;
            return global + 1;
        }
    }
}
