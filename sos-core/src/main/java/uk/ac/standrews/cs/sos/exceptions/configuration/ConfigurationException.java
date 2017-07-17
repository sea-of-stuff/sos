package uk.ac.standrews.cs.sos.exceptions.configuration;

import uk.ac.standrews.cs.sos.exceptions.SOSException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ConfigurationException extends SOSException {

    public ConfigurationException(Throwable throwable) {
        super(throwable);
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
