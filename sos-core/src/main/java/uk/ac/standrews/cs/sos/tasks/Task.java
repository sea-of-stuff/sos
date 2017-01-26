package uk.ac.standrews.cs.sos.tasks;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Task implements Runnable {

    @Override
    public synchronized void run() {
        performAction();
        notify(); // Needed for Sync tasks only
    }

    public abstract void performAction();
}
