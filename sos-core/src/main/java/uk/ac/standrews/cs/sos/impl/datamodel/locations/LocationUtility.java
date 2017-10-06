package uk.ac.standrews.cs.sos.impl.datamodel.locations;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.sos.model.Location;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationUtility {

    /**
     * Return the data at the given location.
     * The method calling this function should ensure that the data object is closed.
     *
     * @param location
     * @return
     */
    public static Data getDataFromLocation(Location location) {

        try {
            return new InputStreamData(location.getSource());
        } catch (IOException e) {
            return new EmptyData();
        }
    }
}
