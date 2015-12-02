package model.implementations.utils;

import java.net.URL;

/**
 * Represents a location in the data-space within the Sea of Stuff.
 * This can be either a location in the local disk or a remote location.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Location {

    /**
     * @return the location of the data
     */
    public abstract URL getLocationPath();

}
