package uk.ac.standrews.cs.sos.instrument;

import uk.ac.standrews.cs.guid.ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum StatsTYPE {

    error,
    none,
    any,

    ping,

    guid_data, guid_manifest, // Time to generate GUIDs for data or manifest
    sha1, sha256, sha384, sha512, md5,

    io, // Time to write/read data, manifests, metadata, etc...
    add_atom, read_atom, replicate_atom,
    add_manifest, read_manifest, replicate_manifest,
    fs_write_file, fs_read_file,

    predicate, predicate_remote, // time to run the predicate function per asset
    predicate_dataset, // time to run the predicate on all the assets
    predicate_prep, // time to prepare before running the predicate for all assets
    predicate_check, // time spent to check if predicate has to be run (for all assets - cumulative)
    predicate_update_context, // time to update context with all the predicate results

    policies,
    policy_apply_dataset,
    scp,

    checkPolicies,
    policy_check_dataset,
    no_valid_policies,

    thread,

    experiment;

    public static StatsTYPE getHashType(ALGORITHM algorithm) {

        switch(algorithm) {
            case SHA1:
                return sha1;
            case SHA256:
                return sha256;
            case SHA384:
                return sha384;
            case SHA512:
                return sha512;
            case MD5:
                return md5;
            default:
                return error;
        }
    }
}
