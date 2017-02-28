package uk.ac.standrews.cs.sos.interfaces.context;

import uk.ac.standrews.cs.sos.interfaces.model.Asset;

import java.util.function.Predicate;

/**
 * Formally, a closure is a function that captures the state of the surrounding environment.
 * We apply the concept of closure to be a function that captures the state of the SOS and it is applied on a given asset.
 *
 * Closures are used to establish whether assets belong to a context or not.
 * A closure will return either true or false when applied.
 * When a closure returns true, then the asset satisfied the closure conditions.
 * Otherwise, the asset does not satisfy the closure.
 *
 * Examples:
 * Closure 1:
 *  Asset.metadata.timestamp == Today

 * Closure 2:
 *  Asset.metadata.size > 300kb
 *
 * Closure 3: Closure 1 AND Closure 2
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Closure extends Predicate<Asset> {

    /**
     * AND this closure with another one
     * @param closure
     * @return
     */
    Closure AND(Closure closure);

    /**
     * OR this closure with another one
     * @param closure
     * @return
     */
    Closure OR(Closure closure);

}
