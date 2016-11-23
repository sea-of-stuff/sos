package uk.ac.standrews.cs.sos.model.context;

import uk.ac.standrews.cs.sos.interfaces.context.Closure;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClosureImpl implements Closure {

    @Override
    public boolean apply(Asset asset) {
        return false;
    }

    @Override
    public Closure AND(Closure closure) {
        return null;
    }

    @Override
    public Closure OR(Closure closure) {
        return null;
    }
}
