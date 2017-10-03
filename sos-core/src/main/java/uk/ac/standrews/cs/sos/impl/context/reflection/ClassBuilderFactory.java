package uk.ac.standrews.cs.sos.impl.context.reflection;

import uk.ac.standrews.cs.sos.exceptions.reflection.ClassBuilderException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ClassBuilderFactory {

    static final String PACKAGE = "uk.ac.standrews.cs.sos.impl.context";

    public static ClassBuilder getClassBuilder(String builder) throws ClassBuilderException {

        switch(builder.toLowerCase()) {

            case "predicate" :
                return new PredicateClassBuilder();

            case "policy" :
                return new PolicyClassBuilder();

            default:
                throw new ClassBuilderException("Unknown class builder type");
        }
    }
}
