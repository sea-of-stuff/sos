package uk.ac.standrews.cs.sos.impl.protocol;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum TaskState {

    INITIALIZED,
    RUNNING,
    SUCCESSFUL,
    UNSUCCESSFUL, // The task completed but with a negative result
    ERROR // The task could not be completed correctly
}
