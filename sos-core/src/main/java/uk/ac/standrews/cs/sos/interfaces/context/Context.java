package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.IGUID;

/**
 * A context in the SOS is defined as a set of information used to characterise a collection of related entities.
 * Entities are assets belonging to the SOS.
 *
 * A context is unique and is made of policies and a closure.
 * Policies are rules that SOS instances should respect about the assets that belong to a given context.
 * A closure defines what assets belong to a context or not.
 *
 * Moreover, contexts can be combines under the boolean operators AND and OR
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Context {

    /**
     * Return the unique identifier for this context
     * @return
     */
    IGUID getGUID();

    /**
     * Return a human-readable name for the context
     * @return
     */
    String getName();

    /**
     * Return a set of policies for this context
     */
    void getContextPolicies();

    /**
     * Return the closure for this context.
     * The closure will define what data belongs to this context and what not.
     */
    Closure getClosure();

    /**
     * Combine this context with another one under the AND logical operator
     * @param context to AND
     * @return a new context
     */
    Context AND(Context context);

    /**
     * * Combine this context with another one under the OR logical operator
     * @param context to OR
     * @return a new context
     */
    Context OR(Context context);
}


