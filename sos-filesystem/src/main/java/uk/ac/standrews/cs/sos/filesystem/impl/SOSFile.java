package uk.ac.standrews.cs.sos.filesystem.impl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.PersistenceException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.persistence.interfaces.IData;
import uk.ac.standrews.cs.fs.store.impl.localfilebased.InputStreamData;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.sos.filesystem.Helper;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.sos.Agent;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.exceptions.StorageException;
import uk.ac.standrews.cs.utils.Error;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFile extends SOSFileSystemObject implements IFile {

    private boolean isCompoundData;
    private Atom atom;
    private Collection<Content> atoms;

    public SOSFile(Agent sos, SOSDirectory parent, IData data) throws PersistenceException {
        super(sos, data);
        this.parent = parent;
        this.isCompoundData = false;

        try {
            InputStream stream = data.getInputStream();
            AtomBuilder builder = new AtomBuilder().setInputStream(stream);

            atom = sos.addAtom(builder);
            metadata = sos.addMetadata(atom);

            VersionBuilder versionBuilder = new VersionBuilder(atom.getContentGUID())
                    .setMetadata(metadata);
            asset = sos.addVersion(versionBuilder);

            this.guid = asset.guid();
        } catch (StorageException | IOException |
                ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (ManifestNotMadeException | SOSMetadataException e) {
            e.printStackTrace();
        }

    }

    public SOSFile(Agent sos)  {
        super(sos);
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating SOS File - Compound Data: true");

        this.isCompoundData = true;
        this.atoms = new ArrayList<>();
        // TODO - set version

        // this.guid = version.guid();
    }

    public SOSFile(Agent sos, Asset asset, Atom atom) {
        super(sos);

        this.asset = asset;
        this.atom = atom;

        Collection<IGUID> meta = asset.getMetadata();
        if (meta != null && !meta.isEmpty()) {
            IGUID metaGUID = (IGUID) meta.toArray()[0];
            this.metadata = sos.getMetadata(metaGUID);
        }

        this.guid = asset.guid();
    }

    public SOSFile(Agent sos, SOSDirectory parent, IData data, SOSFile previous) throws PersistenceException {
        super(sos, data);
        this.parent = parent;
        this.isCompoundData = false;

        try {
            InputStream stream = data.getInputStream();
            AtomBuilder builder = new AtomBuilder().setInputStream(stream);
            atom = sos.addAtom(builder);
            metadata = sos.addMetadata(atom);

            boolean previousVersionDiffers = checkPreviousDiffers(atom.getContentGUID());
            if (previousVersionDiffers) {

                Collection<IGUID> previousVersion = new ArrayList<>();
                previousVersion.add(previous.getAsset().getVersionGUID());
                VersionBuilder versionBuilder = new VersionBuilder(atom.getContentGUID())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion)
                        .setMetadata(metadata);

                asset = sos.addVersion(versionBuilder);

                this.guid = asset.guid();
                this.previous = previous;
            } else {
                System.out.println("This create an identical new object to previous. Can be optimised to occupy less memory");
                this.previous = previous.getPreviousFILE();
            }

        } catch (StorageException | IOException |
                ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (ManifestNotMadeException | SOSMetadataException e) {
            e.printStackTrace();
        }

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

        if (metadata != null) {
            String s_creationtime = metadata.getProperty("Timestamp").trim();
            creationtime = Long.parseLong(s_creationtime);
        }

        return Helper.UnixTimeToFileTime(creationtime);
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        long modtime = 0;

        if (metadata != null) {
            String s_modtime = metadata.getProperty("Timestamp").trim();
            modtime = Long.parseLong(s_modtime);
        }

        return Helper.UnixTimeToFileTime(modtime);
    }

    @Override
    public void update(IData data) {
        throw new NotImplementedException();
    }

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

        if (! isCompoundData) {
            retval = atom.getContentGUID();
        } else {
            //Compound compound = addAtomsInCompound(atoms);
            //builder = new VersionBuilder(compound.getContentGUID());
        }

        return retval;
    }


    // this will differ based on whether it is a single atom or a compound of atoms sos.getData(guid);
    // NOTE: idea - have a isChunked() method. If that method returns true, then reify returns data until null (no more chunks)
    @Override
    public IData reify() {

        int size = 0;
        if (metadata != null) {
            String s_size = metadata.getProperty("Size").trim();
            size = (int) Long.parseLong(s_size);
        }

        try (InputStream stream = sos.getAtomContent(atom)) {
            IData data = new InputStreamData(stream, size);
            return data;
        } catch (IOException e) {
            return null;
        }
    }

    public SOSFile getPreviousFILE() {
        return (SOSFile) previous;
    }

    public IDirectory getParent() {
        return parent;
    }

    @Override
    public void setParent(IDirectory parent) {
        this.parent = (SOSDirectory) parent;
    }

}
