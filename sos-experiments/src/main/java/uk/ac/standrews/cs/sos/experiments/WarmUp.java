package uk.ac.standrews.cs.sos.experiments;

/**
 * Code to warm up the JVM.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WarmUp {

    public static void run() {
        long start = System.nanoTime();
        for (int i = 0; i < 100000000; i++) {
            Clazz clazz = new Clazz();
            clazz.method();
        }
        System.out.println("JVM WarmUp finished in " + (System.nanoTime() - start) / 1000000000.0 + " seconds");
    }

    private static class Clazz {

        public void method() {}
    }
}
