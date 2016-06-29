package uk.ac.standrews.cs.sos.storage.interfaces;

import uk.ac.standrews.cs.sos.storage.exceptions.BindingAbsentException;

import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SOSDirectory extends SOSStatefulObject {

    SOSStatefulObject get(String name) throws BindingAbsentException;

    boolean contains(String name);

    void addSOSFile(SOSFile file, String name);

    void addSOSDirectory(SOSDirectory directory, String name);

    void remove(String name) throws BindingAbsentException;

    Iterator<SOSStatefulObject> getIterator();

    boolean mkdir();

}
