package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.actors.*;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.model.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.model.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Set;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAgent implements Agent {

    private Storage storage;
    private DDS dds;
    private MMS mms;
    private CMS cms;
    private RMS rms;

    private SOSAgent(Storage storage, DDS dds, MMS mms, CMS cms, RMS rms) {
        this.storage = storage;
        this.dds = dds;
        this.mms = mms;
        this.cms = cms;
        this.rms = rms;
    }

    private static SOSAgent instance;
    public static SOSAgent instance(Storage storage, DDS dds, MMS mms, CMS cms, RMS rms) {
        if (instance == null) {
            instance = new SOSAgent(storage, dds, mms, cms, rms);
        }

        return instance;
    }

    public static SOSAgent instance() {
        return instance;
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws StorageException, ManifestPersistException {
        Atom manifest = storage.addAtom(atomBuilder, false, new DDSNotificationInfo().setNotifyDDSNodes(true)).x;
        return manifest;
    }

    @Override
    public Compound addCompound(CompoundBuilder compoundBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundType type = compoundBuilder.getType();
        Set<Content> contents = compoundBuilder.getContents();
        CompoundManifest compound = ManifestFactory.createCompoundManifest(type, contents, rms.active());

        addManifest(compound);

        return compound;
    }

    @Override
    public Version addVersion(VersionBuilder versionBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Set<IGUID> prevs = versionBuilder.getPreviousCollection();
        IGUID metadata = versionBuilder.getMetadataCollection();

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, rms.active());
        addManifest(manifest);

        cms.runPredicates(PredicateComputationType.AFTER_STORING, manifest);

        return manifest;
    }

    @Override
    public Version addData(VersionBuilder versionBuilder) {

        try {
            // TODO - pre_store predicate with any policy

            Atom atom = addAtom(versionBuilder.getAtomBuilder());
            versionBuilder.setContent(atom.guid());

            Version manifest = addVersion(versionBuilder);

            return manifest;
        } catch (StorageException e) {
            e.printStackTrace();
        } catch (ManifestPersistException e) {
            e.printStackTrace();
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public InputStream getData(Version version) throws AtomNotFoundException {

        IGUID content = version.getContentGUID();
        return storage.getAtomContent(content);
    }

    @Override
    public Version addCollection(VersionBuilder versionBuilder) {

        try {
            Compound compound = addCompound(versionBuilder.getCompoundBuilder());
            versionBuilder.setContent(compound.guid());

            Version manifest = addVersion(versionBuilder);

            return manifest;
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        } catch (ManifestPersistException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return
     */
    @Override
    public InputStream getAtomContent(Atom atom) throws AtomNotFoundException {
        return storage.getAtomContent(atom);
    }

    @Override
    public void addManifest(Manifest manifest) throws ManifestPersistException {
        dds.addManifest(manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return dds.getManifest(guid);
    }

    @Override
    public boolean verifyManifest(Role role, Manifest manifest) throws ManifestVerificationException {
        boolean success = manifest.verifySignature(role);
        return success;
    }

    @Override
    public Metadata addMetadata(InputStream inputStream) throws MetadataException {

        Metadata metadata = mms.processMetadata(inputStream);
        mms.addMetadata(metadata);

        return metadata;
    }

    @Override
    public Metadata getMetadata(IGUID guid) throws MetadataNotFoundException {
        Metadata metadata = mms.getMetadata(guid);
        return metadata;
    }

}
