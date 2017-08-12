package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.*;

/**
 * The Agent is one of the node roles within the Sea of Stuff.
 *
 * End-users interact with the SOS via the Agent.
 *
 * The Agent supports the following operations:
 * - pushing data/manifests to the SOS
 * - get data/manifests from the SOS
 * - find data in the SOS
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Agent {

    /**
     * Adds data to the Sea of Stuff as an atom.
     *
     * @param atomBuilder for this atom
     * @return the added atom
     * @throws StorageException
     * @throws ManifestPersistException
     *
     * @apiNote the data will not processed through contexts
     */
    Atom addAtom(AtomBuilder atomBuilder) throws ManifestPersistException, DataStorageException;

    SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException;

    /**
     * Adds a Compound to the Sea of Stuff.
     *
     * @param compoundBuilder for this compound.
     * @return the added compound.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     * @see Manifest
     *
     * @deprecated - use addCollection(VersionBuilder)?
     */
    Compound addCompound(CompoundBuilder compoundBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException;

    SecureCompound addSecureCompound(CompoundBuilder compoundBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException;

    /**
     * Adds a version of an asset to the Sea of Stuff.
     *
     * @param versionBuilder for this version
     * @return Version for the added asset.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     */
    Version addVersion(VersionBuilder versionBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException;

    Version addData(VersionBuilder versionBuilder); // TODO - exceptions
    Data getData(Version version) throws AtomNotFoundException;
    Version addCollection(VersionBuilder versionBuilder); // TODO - exceptions

    /**
     * Get the data of an Atom.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     * @throws AtomNotFoundException
     */
    Data getAtomContent(Atom atom) throws AtomNotFoundException;

    /**
     * Get the manifest matching the given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws ManifestNotFoundException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     *
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException;

    /**
     * Generate and add metadata for this atom
     *
     * @param data used to generate the metadata
     * @return the metadata generated
     * @throws MetadataException if the metadata could not be generated
     */
    Metadata addMetadata(Data data) throws MetadataException;

    /**
     * Get the metadata mapped to the specified guid
     *
     * @param guid for the metadata
     * @return SOSMetadata mapped with the guid
     */
    Metadata getMetadata(IGUID guid) throws MetadataNotFoundException;

    /**
     * Verify the manifest signature against the given role.
     *
     * @param role                          used to verify the manifest
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     * @throws SignatureException if the manifest could not be verified
     */
    boolean verifyManifestSignature(Role role, Manifest manifest) throws SignatureException;

    /**
     * Verify the integrity of the manifest's GUID against the
     * content of the manifest.
     *
     * Hash-based verification ensures that a file has not been corrupted by
     * comparing the data's hash value to a previously calculated value.
     * If these values match, the data is presumed to be unmodified.
     * Due to the nature of hash functions, hash collisions may result
     * in false positives, but the likelihood of collisions is
     * often negligible with random corruption. (https://en.wikipedia.org/wiki/File_verification)
     *
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     */
    boolean verifyManifestIntegrity(Manifest manifest) throws ManifestVerificationException;

    /**
     * Get the propery value for the given manifest matching GUID
     * The manifest MUST be a version manifest
     *
     * TODO - do not return a simple object, but something custom-made (MetaObject)
     *
     * @param guid
     * @param property
     * @return
     * @throws ManifestNotFoundException
     * @throws MetadataNotFoundException
     */
    Object getMetaProperty(IGUID guid, String property) throws ManifestNotFoundException, MetadataNotFoundException;
}
