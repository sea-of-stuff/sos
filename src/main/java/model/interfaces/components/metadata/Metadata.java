package model.interfaces.components.metadata;

/**
 * Metadata is data about assets, compounds or atoms that can be used to
 * facilitate data retrieval, usage, and management.
 * We classify metadata of intrinsic and extrinsic nature based on how it relates to an atom.
 *
 * Metadata is stored in the data-space partition of the Sea of Stuff and it is locatable
 * through manifests.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Metadata {

    /**
     * i.e. tag, closure, etc
     *
     * @return
     */
    String getType();


    // TODO
}
