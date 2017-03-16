package uk.ac.standrews.cs.sos.interfaces.model;

import uk.ac.standrews.cs.IGUID;

/**
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
