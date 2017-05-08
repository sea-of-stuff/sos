package uk.ac.standrews.cs.sos.filesystem.impl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.InputStreamData;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.sos.actors.Agent;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants;
import uk.ac.standrews.cs.sos.filesystem.utils.Helper;
import uk.ac.standrews.cs.sos.impl.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants.META_SIZE;
import static uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants.META_TIMESTAMP;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {

    private static final int DEFAULT_MAX_FILESIZE = 4096;

    private boolean isCompoundData;
    private Atom atom;

    // References to multiple atoms, in case a file is de-composed in multiple atoms
    private Set<Content> atoms;
    private Compound compound;

    /**
     * Create a file object inside a parent directory and with some data
     *
     * @param sos
     * @param parent containing the new file
     * @param data for the file
     * @param previous
     * @throws PersistenceException
     */
    public SOSFile(Agent sos, SOSDirectory parent, IData data, SOSFile previous) throws PersistenceException {
        super(sos, data);

        this.parent = parent;
        this.isCompoundData = false;

        try {
            InputStream stream = data.getInputStream();
            AtomBuilder atomBuilder = new AtomBuilder().setInputStream(stream);
            this.metadata = sos.addMetadata(stream); // Metadata is generated, saved and returned by the SOS

            VersionBuilder versionBuilder = new VersionBuilder()
                    .setAtomBuilder(atomBuilder)
                    .setMetadata(metadata);

            if (previous != null) {
                boolean previousVersionDiffers = previousAssetDiffers(atom.guid());
                if (previousVersionDiffers) {
                    Set<IGUID> previousVersion = new LinkedHashSet<>();
                    previousVersion.add(previous.getVersion().getVersionGUID());

                    versionBuilder.setInvariant(previous.getInvariant())
                            .setPrevious(previousVersion);

                    this.previous = previous;
                } else {
                    System.out.println("WARN - This create an identical new object to previous. Can be optimised to occupy less memory");
                    this.previous = previous.getPreviousObject();
                }
            }

            this.version = sos.addData(versionBuilder);
            this.guid = version.guid();

        } catch (IOException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (MetadataException e) {
            e.printStackTrace();
        }

    }

    public SOSFile(Agent sos)  {
        super(sos);
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating SOS File - Compound Data: true");

        this.isCompoundData = true;
        this.atoms = new LinkedHashSet<>();
    }

    /**
     * Use this constructor to make a File reference.
     * That is, the file and the data are already in the SOS, but we need to get a handle for it.
     *
     * @param sos
     * @param version for this atom
     */
    public SOSFile(Agent sos, Version version) {
        super(sos);

        this.isCompoundData = false;
        this.version = version;

        IGUID contentGUID = version.getContentGUID();
        try {
            this.atom = (Atom) sos.getManifest(contentGUID);
        } catch (ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS File - Unable to get atom content for GUID " + contentGUID);
        }

        IGUID meta = version.getMetadata();
        if (meta != null && !meta.isInvalid()) {
            try {
                this.metadata = sos.getMetadata(meta);
            } catch (MetadataNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS File - Unable to get metadata for GUID " + meta);
            }
        }

        this.guid = version.guid();
    }

    @Override
    public IAttributes getAttributes() {

        IAttributes dummyAttributes = new Attributes(FileSystemConstants.ISFILE + Attributes.EQUALS + "true" + Attributes.SEPARATOR +
                FileSystemConstants.CONTENT + Attributes.EQUALS + "text" + Attributes.SEPARATOR );

        return dummyAttributes;
    }

    @Override
    public void setAttributes(IAttributes attributes) {
        // This will allow to explitly set new attributes, resulting in a new version of the data
        // or we could have the data pointing an version, so the file does not need to change as the metadata does
        ErrorHandling.hardError("unimplemented method");
    }

    @Override
    public long getCreationTime() throws AccessFailureException {
        // FIXME - we should probably get the timestamp of the first version, but it will be a complex operation,
        // so for the moment we just take the timestamp of this asset version
        return getModificationTime();
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        long modtime = 0;

        if (metadata != null && metadata.hasProperty(META_TIMESTAMP)) {
            modtime = metadata.getPropertyAsInteger(META_TIMESTAMP);
        }

        return Helper.UnixTimeToFileTime(modtime);
    }

    /**
     * This method is not implemented because SOS objects are immutable
     * @param data
     */
    @Override
    public void update(IData data) {
        throw new NotImplementedException();
    }

    // TODO - add comment to explain what this method does and why and how
    @Override
    public void append(IData data) {
        if (!isCompoundData)
            return;

        try {
            AtomBuilder builder = new AtomBuilder().setInputStream(data.getInputStream());
            Atom atom = sos.addAtom(builder);
            Content content = new ContentImpl(atom.guid());
            atoms.add(content);

        } catch (ManifestPersistException | IOException | StorageException e) {
            e.printStackTrace();
        }
    }

    protected IGUID getContentGUID() {
        IGUID retval = null;

        if (!isCompoundData) {
            retval = atom.guid();
        } else {
            // TODO - guid of compound
        }

        return retval;
    }


    // NOTE: this method seems to be called a lot of times, thus slowing down a bit of everything
    // this will differ based on whether it is a single atom or a compound of atoms sos.getData(guid);
    //
    // IDEA - have a isChunked() method. If that method returns true, then reify returns data until null (no more chunks)
    @Override
    public IData reify() {

        int size = DEFAULT_MAX_FILESIZE;
        if (metadata != null && metadata.hasProperty(META_SIZE)) {
            size = metadata.getPropertyAsInteger(META_SIZE);
        }

        try (InputStream stream = sos.getAtomContent(atom)){

            return new InputStreamData(stream, size);
        } catch (AtomNotFoundException | IOException e) {
            e.printStackTrace(); // TODO - define EmptyDATA() OBJECT
        }

        return null;
    }

}
