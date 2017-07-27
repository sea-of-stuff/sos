package uk.ac.standrews.cs.sos.experiments;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WarmUp {

    public static void run() {
        long start = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            Dummy dummy = new Dummy();
            dummy.m();
        }
        System.out.println("WarmUp step finished in " + (System.nanoTime() - start) / 1000000000.0 + " seconds");
    }

    private static class Dummy {

        public void m() {}
    }
}
