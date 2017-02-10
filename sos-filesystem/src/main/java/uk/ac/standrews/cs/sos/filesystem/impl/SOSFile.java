package uk.ac.standrews.cs.sos.filesystem.impl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.InputStreamData;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants;
import uk.ac.standrews.cs.sos.filesystem.utils.Helper;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AssetBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.utils.Error;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {

    private static final int DEFAULT_MAX_FILESIZE = 4096;

    private boolean isCompoundData;
    private Atom atom;

    // References to multiple atoms, in case a file is de-composed in multiple atoms
    private Set<Content> atoms;

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
            AtomBuilder builder = new AtomBuilder().setInputStream(stream);

            this.atom = sos.addAtom(builder); // Atom is saved and manifest returned by the SOS
            this.metadata = sos.addMetadata(atom); // Metadata is generated, saved and returned by the SOS

            AssetBuilder assetBuilder = new AssetBuilder(atom.getContentGUID()).setMetadata(metadata);

            if (previous != null) {
                boolean previousVersionDiffers = previousAssetDiffers(atom.getContentGUID());
                if (previousVersionDiffers) {
                    Set<IGUID> previousVersion = new LinkedHashSet<>();
                    previousVersion.add(previous.getAsset().getVersionGUID());

                    assetBuilder.setInvariant(previous.getInvariant())
                            .setPrevious(previousVersion);

                    this.previous = previous;
                } else {
                    System.out.println("WARN - This create an identical new object to previous. Can be optimised to occupy less memory");
                    this.previous = previous.getPreviousObject();
                }
            }

            this.asset = sos.addAsset(assetBuilder);
            this.guid = asset.guid();

        } catch (StorageException | IOException | ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (ManifestNotMadeException | MetadataException e) {
            e.printStackTrace();
        }

    }

    public SOSFile(Agent sos)  {
        super(sos);
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating SOS File - Compound Data: true");

        this.isCompoundData = true;
        this.atoms = new LinkedHashSet<>();
        // TODO - set version

        // this.guid = version.guid();
    }

    /**
     * Use this constructor to make a File reference.
     * That is, the file and the data are already in the SOS, but we need to get a handle for it.
     *
     * @param sos
     * @param asset for this atom
     */
    public SOSFile(Agent sos, Asset asset) {
        super(sos);

        this.isCompoundData = false;
        this.asset = asset;

        IGUID contentGUID = asset.getContentGUID();
        try {
            this.atom = (Atom) sos.getManifest(contentGUID);
        } catch (ManifestNotFoundException e) {
            SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS File - Unable to get atom content for GUID " + contentGUID);
        }

        IGUID meta = asset.getMetadata();
        if (meta != null && !meta.isInvalid()) {
            try {
                this.metadata = sos.getMetadata(meta);
            } catch (MetadataNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "WEBDAV - Creating SOS File - Unable to get metadata for GUID " + meta);
            }
        }

        this.guid = asset.guid();
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
        Error.hardError("unimplemented method");
    }

    @Override
    public long getCreationTime() throws AccessFailureException {
        long creationtime = 0;

        // FIXME - we should probably get the timestamp of the first version, but it will be a complex operation,
        // so for the moment we just take the timestamp of this asset version
        if (metadata != null && metadata.hasProperty("Timestamp")) {
            String s_creationtime = metadata.getProperty("Timestamp").trim();
            creationtime = Long.parseLong(s_creationtime);
        }

        return Helper.UnixTimeToFileTime(creationtime);
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        long modtime = 0;

        if (metadata != null && metadata.hasProperty("Timestamp")) {
            String s_modtime = metadata.getProperty("Timestamp").trim();
            modtime = Long.parseLong(s_modtime);
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
            Atom atom = sos.addAtom(new AtomBuilder().setInputStream(data.getInputStream()));
            IGUID guid = atom.getContentGUID();
            Content content = new Content(guid);
            atoms.add(content);

        } catch (ManifestPersistException | IOException | StorageException e) {
            e.printStackTrace();
        }
    }

    protected IGUID getContentGUID() {
        IGUID retval = null;

        if (!isCompoundData) {
            retval = atom.getContentGUID();
        } else {
            //Compound compound = addAtomsInCompound(atoms);
            //builder = new VersionBuilder(compound.getContentGUID());
        }

        return retval;
    }


    // NOTE: this method seems to be called a lot of times, thus slowing down a bit of everything
    // this will differ based on whether it is a single atom or a compound of atoms sos.getData(guid);
    // NOTE: idea - have a isChunked() method. If that method returns true, then reify returns data until null (no more chunks)
    @Override
    public IData reify() {

        int size = DEFAULT_MAX_FILESIZE;
        if (metadata != null && metadata.hasProperty("Size")) {
            String s_size = metadata.getProperty("Size").trim();
            size = Integer.parseInt(s_size);
        }

        try (InputStream stream = sos.getAtomContent(atom)){

            return new InputStreamData(stream, size);
        } catch (AtomNotFoundException | IOException e) {
            e.printStackTrace(); // TODO - define EmptyDATA() OBJECT
        }

        return null;
    }

}
