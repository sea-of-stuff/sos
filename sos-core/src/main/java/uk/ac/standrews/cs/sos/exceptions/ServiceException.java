package uk.ac.standrews.cs.sos.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServiceException extends SOSException {

    public ServiceException(SERVICE service, String message) {
        super(service.toString() + message);
    }

    public ServiceException(SERVICE service, String message, Throwable throwable) {
        super(service.toString() + message + " --- " + throwable.getMessage(), throwable);
    }

    public ServiceException(SERVICE service, Throwable throwable) {
        super(service.toString() + throwable.getMessage(), throwable);
    }

    public enum SERVICE {
        AGENT("AGENT"),
        CONTEXT("CONTEXT"),
        MANIFESTS_DATA("MANIFESTS_DATA"),
        METADATA("METADATA"),
        NODE("NODE"),
        STORAGE("STORAGE"),
        USRO("USRO");

        private String service;
        SERVICE(String service) {
            this.service = service;
        }

        @Override
        public String toString() {
            return service + " -- ";
        }
    }
}