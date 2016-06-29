package uk.ac.standrews.cs.sos.storage.interfaces;

/**
 * @author al
 */
public interface INameObjectBinding {
    /**
     * Gets the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the IAttributedStatefulObject.
     *
     * @return the IAttributedStatefulObject
     */
    SOSStatefulObject getObject();
}
