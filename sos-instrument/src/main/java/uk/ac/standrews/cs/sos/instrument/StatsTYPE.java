package uk.ac.standrews.cs.sos.instrument;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum StatsTYPE {

    none,
    any,

    io, // Time to write/read data, manifests, metadata, etc...
    add_atom, read_atom,
    add_manifest, read_manifest,

    predicate, // time to run the predicate function per asset
    predicate_dataset, // time to run the predicate on all the assets
    predicate_prep, // time to prepare before running the predicate for all assets
    predicate_check, // time spent to check if predicate has to be run (for all assets - cumulative)
    predicate_update_context, // time to update context with all the predicate results

    policies,
    policy_apply_dataset,

    checkPolicies,
    policy_check_dataset,

    experiment
}
