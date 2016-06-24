package uk.ac.standrews.cs.sos.storage.interfaces;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSDirectory extends SOSStatefulObject {

    SOSFile addSOSFile(String fileName);

    SOSDirectory addSOSDirectory(String directoryName);

    Iterator<SOSStatefulObject> getIterator();

    boolean mkdirs();
}
