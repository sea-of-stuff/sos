package uk.ac.standrews.cs.sos.impl.context.reflection;

import uk.ac.standrews.cs.sos.model.ComputationalUnit;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ClassBuilder {

    String constructClass(ComputationalUnit computationalUnit) throws IOException;
}
