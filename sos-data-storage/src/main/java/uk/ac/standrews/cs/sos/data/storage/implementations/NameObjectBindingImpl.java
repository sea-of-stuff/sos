package uk.ac.standrews.cs.sos.storage.implementations;

import uk.ac.standrews.cs.sos.storage.interfaces.NameObjectBinding;
import uk.ac.standrews.cs.sos.storage.interfaces.StatefulObject;

/**
 * Implements a binding between a logical name and an StatefulObject
 * A Directory contains a collection of these bindings.
 *
 * @author al
 */
public class NameObjectBindingImpl implements NameObjectBinding {

    private String name;
    private StatefulObject obj;

    /**
     * Creates a binding between a name and a GUID.
     *
     * @param name the name
     * @param obj an StatefulObject
     */
    public NameObjectBindingImpl(String name, StatefulObject obj) {
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
     * Gets the StatefulObject.
     *
     * @return the StatefulObject
     */
    public StatefulObject getObject() {
        return obj;
    }

}

