package uk.ac.standrews.cs.sos.storage.implementations;

import uk.ac.standrews.cs.sos.storage.interfaces.INameObjectBinding;
import uk.ac.standrews.cs.sos.storage.interfaces.SOSStatefulObject;

/**
 * Implements a binding between a logical name and an IAttributedStatefulObject
 * A Directory contains a collection of these bindings.
 *
 * @author al
 */
public class NameObjectBinding implements INameObjectBinding {

    private String name;
    private SOSStatefulObject obj;

    /**
     * Creates a binding between a name and a GUID.
     *
     * @param name the name
     * @param obj an IAttributedStatefulObject
     */
    public NameObjectBinding(String name, SOSStatefulObject obj) {
        this.name = name;
        this.obj = obj;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the IAttributedStatefulObject.
     *
     * @return the IAttributedStatefulObject
     */
    public SOSStatefulObject getObject() {
        return obj;
    }

}

