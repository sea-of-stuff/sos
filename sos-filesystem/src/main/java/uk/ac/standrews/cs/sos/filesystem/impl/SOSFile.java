package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.InputStreamData;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemException;
import uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants;
import uk.ac.standrews.cs.sos.filesystem.utils.Helper;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Compound;
import uk.ac.standrews.cs.sos.model.Content;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
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
    public SOSFile(Agent sos, SOSDirectory parent, IData data, SOSFile previous) throws PersistenceException, SOSFileSystemException {
        super(sos, data);

        this.parent = parent;
        this.isCompoundData = false;

        try (InputStream inputStream = data.getInputStream();
             ByteArrayOutputStream baos = IO.InputStreamToByteArrayOutputStream(inputStream);
             InputStream streamForAtom = new ByteArrayInputStream(baos.toByteArray());
             InputStream streamForMetadata = new ByteArrayInputStream(baos.toByteArray())) {

            AtomBuilder atomBuilder = new AtomBuilder().setData(new uk.ac.standrews.cs.castore.data.InputStreamData(streamForAtom));

            this.atom = sos.addAtom(atomBuilder); // Atom is saved and manifest returned by the SOS
            this.metadata = sos.addMetadata(new uk.ac.standrews.cs.castore.data.InputStreamData(streamForMetadata), null); // Metadata is generated, saved and returned by the SOS

            VersionBuilder versionBuilder = new VersionBuilder()
                    .setAtomBuilder(atomBuilder)
                    .setMetadata(metadata);

            if (previous != null) {
                boolean previousVersionDiffers = previousAssetDiffers(atom.guid());
                if (previousVersionDiffers) {
                    Set<IGUID> previousVersion = new LinkedHashSet<>();
                    previousVersion.add(previous.getVersion().version());

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
        } catch (ServiceException e) {
            throw new SOSFileSystemException();
        }

    }

    public SOSFile(Agent sos)  {
        super(sos);
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating SOS CreateFile - Compound Data: true");

        this.isCompoundData = true;
        this.atoms = new LinkedHashSet<>();
    }

    /**
     * Use this constructor to make a CreateFile reference.
     * That is, the file and the data are already in the SOS, but we need to get a handle for it.
     *
     * @param sos
     * @param version for this atom
     */
    public SOSFile(Agent sos, Version version) {
        super(sos);

        this.isCompoundData = false;
        this.version = version;

        IGUID contentGUID = version.content();
        try {
            this.atom = (Atom) sos.getManifest(contentGUID);
        } catch (ServiceException e) {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS CreateFile - Unable to get atom content for GUID " + contentGUID);
        }

        try {
            this.metadata = sos.getMetadata(version);
        } catch (ServiceException e) {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS CreateFile - Unable to get metadata for version with GUID " + version.guid().toMultiHash());
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

    /**
     * Get the creation time for this object from its metadata
     * The creation time matches the modification time
     *
     * @return
     * @throws AccessFailureException
     */
    @Override
    public long getCreationTime() throws AccessFailureException {
        // Note - we should probably get the timestamp of the first version, but it will be a complex operation,
        // so for the moment we just take the timestamp of this asset version
        return getModificationTime();
    }

    /**
     * Get the last modification time from the metadata of this object's version's metadata
     *
     * @return
     * @throws AccessFailureException
     */
    @Override
    public long getModificationTime() throws AccessFailureException {
        long modtime = 0;

        if (metadata != null && metadata.hasProperty(META_TIMESTAMP)) {
            modtime = ZonedDateTime.parse(metadata.getPropertyAsString(META_TIMESTAMP)).toEpochSecond() * 1000;
        }

        return Helper.UnixTimeToFileTime(modtime);
    }

    /**
     * This method is not implemented because SOS objects are immutable
     * @param data
     */
    @Override
    public void update(IData data) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * TODO - add comment to explain what this method does and why and how
     * @param data
     */
    @Override
    public void append(IData data) {
        if (!isCompoundData)
            return;

        try {
            AtomBuilder builder = new AtomBuilder().setData(new uk.ac.standrews.cs.castore.data.InputStreamData(data.getInputStream()));
            Atom atom = sos.addAtom(builder);
            Content content = new ContentImpl(atom.guid());
            atoms.add(content);

        } catch (ServiceException | IOException e) {
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

        long size = DEFAULT_MAX_FILESIZE;
        if (metadata != null && metadata.hasProperty(META_SIZE)) {
            size = metadata.getPropertyAsLong(META_SIZE);
        }

        try (Data data = sos.getAtomContent(atom)){

            return new InputStreamData(data.getInputStream(), (int) size);

        } catch (Exception e) {
            e.printStackTrace(); // TODO - define EmptyDATA() OBJECT
        }

        return null;
    }

}
