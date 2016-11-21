package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.interfaces.context.Context;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextImpl implements Context {

    private final IGUID guid;
    private final String name;

    private Closure closure;

    public ContextImpl(String name, Closure closure) {
        this.name = name;
        this.closure = closure;

        guid = GUIDFactory.generateRandomGUID();
    }

    @Override
    public IGUID getGUID() {
        return guid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void getContextPolicies() {

    }

    @Override
    public Closure getClosure() {
        return closure;
    }

    @Override
    public Context AND(Context context) {
        return null;
    }

    @Override
    public Context OR(Context context) {
        return null;
    }
}
