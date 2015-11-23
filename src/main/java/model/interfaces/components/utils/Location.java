package model.interfaces.components.utils;

import java.nio.file.Path;

/**
 * Represents a location in the data-space within the Sea of Stuff.
 * This can be either a location in the local disk or a remote location.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Location {

    /**
     * @return the location of the data
     */
    Path getLocationPath();

}
