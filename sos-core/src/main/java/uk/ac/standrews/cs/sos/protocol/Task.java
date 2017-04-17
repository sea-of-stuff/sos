package uk.ac.standrews.cs.sos.protocol;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Task implements Runnable {

    @Override
    public synchronized void run() {
        performAction();
        notify(); // Awake the SYNC tasks from their wait state
    }

    public abstract void performAction();

}
