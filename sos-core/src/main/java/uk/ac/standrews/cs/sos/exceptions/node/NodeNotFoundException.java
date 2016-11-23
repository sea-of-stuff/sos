package uk.ac.standrews.cs.sos.exceptions.node;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class NodeNotFoundException extends SOSException {

    public NodeNotFoundException(Throwable throwable) {
        super(throwable);
    }
}