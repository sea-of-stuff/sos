package model.interfaces.entities;

import model.interfaces.components.utils.GUID;

/**
 * An Asset is identified by an asset GUID. Unlike other GUIDs they are not
 * derived from contents. Instead an asset GUID is good for all time
 * irrespective of the asset's contents.
 * Assets do not contain data - they refer to unions - and they are used to
 * assert commonality over a history of changes of unions.
 *
 * @see GUID
 * @see ?? - content TODO
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Asset {

    // TODO - refactor and avoid using Union

    // getContent(); // return content of the asset


}
