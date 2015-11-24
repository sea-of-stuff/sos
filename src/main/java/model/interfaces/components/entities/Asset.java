package model.interfaces.components.entities;

import model.implementations.utils.GUID;

/**
 * An Asset is identified by an asset GUID. Unlike other GUIDs they are not
 * derived from contents. Instead an asset GUID is good for all time
 * irrespective of the asset's contents.
 * Assets do not contain data - they refer to unions - and they are used to
 * assert commonality over a history of changes of unions.
 *
 * @see GUID
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Asset {

    // TODO

    // getContent(); // return content of the asset


}
