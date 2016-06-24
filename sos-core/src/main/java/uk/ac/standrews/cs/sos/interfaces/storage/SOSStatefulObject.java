package uk.ac.standrews.cs.sos.interfaces.storage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSStatefulObject {

    SOSDirectory getParent();

    boolean exists();

    String getName();

    String getPathname();

    long lastModified();
}
