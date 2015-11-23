package factories;

import interfaces.components.Location;
import interfaces.entities.Compound;

import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundFactory {

    /**
     *
     * @param unions
     * @return
     */
    Compound makeCompound(List<Union> unions);

    /**
     *
     * @param location
     * @return
     */
    Compound makeCompound(Location location);
}
