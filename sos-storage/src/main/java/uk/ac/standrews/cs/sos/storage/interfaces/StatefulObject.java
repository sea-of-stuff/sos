package uk.ac.standrews.cs.sos.storage.interfaces;

import uk.ac.standrews.cs.sos.storage.exceptions.PersistenceException;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface StatefulObject {

    Directory getParent();

    boolean exists();

    String getName();

    String getPathname();

    long lastModified();

    File toFile();

    void persist() throws PersistenceException;

    boolean isImmutable();

}
