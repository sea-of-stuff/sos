package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;

/**
 * This interface defines the content unit used by compounds
 *
 * Contents are defines as a pair of GUID and arbitrary string labels
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Content {

    /**
     * Gets the GUID of the content.
     *
     * @return GUID of the content.
     */
    IGUID getGUID();

    /**
     * Gets the label of this content.
     *
     * @return label of the content.
     */
    String getLabel();
}
