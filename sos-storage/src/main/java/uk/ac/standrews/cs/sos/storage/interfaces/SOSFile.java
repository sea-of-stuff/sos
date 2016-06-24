package uk.ac.standrews.cs.sos.storage.interfaces;

import java.io.File;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSFile extends SOSStatefulObject {

    File toFile();
}
