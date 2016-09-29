package uk.ac.standrews.cs.sos.app;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MAISOSException extends Exception {

    public MAISOSException(Exception exception) {
        System.err.println("An error occurred: " + exception.getMessage());
    }
}
