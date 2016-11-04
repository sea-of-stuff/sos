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
import uk.ac.standrews.cs.sos.filesystem.FileSystemConstants;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
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

    boolean isCompoundData;
    private Atom atom;
    private Collection<Content> atoms;

    public SOSFile(Client sos, SOSDirectory parent, IData data) throws PersistenceException {
        super(sos, data);
        this.parent = parent;
        this.isCompoundData = false;

        try {
            InputStream stream = data.getInputStream();
            AtomBuilder builder = new AtomBuilder().setInputStream(stream);

            atom = sos.addAtom(builder);
            version = sos.addVersion(new VersionBuilder(atom.getContentGUID()));

        } catch (StorageException | IOException |
                ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        }

        this.guid = getContentGUID();
    }

    public SOSFile(Client sos)  {
        super(sos);
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Creating SOS File - Compound Data: true");

        this.isCompoundData = true;
        this.atoms = new ArrayList<>();
        // TODO - set version

        this.guid = getContentGUID();
    }

    public SOSFile(Client sos, Version version, Atom atom) {
        super(sos);

        this.version = version;
        this.atom = atom;

        this.guid = getContentGUID();
    }

    public SOSFile(Client sos, SOSDirectory parent, IData data, SOSFile previous) throws PersistenceException {
        super(sos, data);
        this.parent = parent;
        this.isCompoundData = false;

        try {
            InputStream stream = data.getInputStream();
            AtomBuilder builder = new AtomBuilder().setInputStream(stream);
            atom = sos.addAtom(builder);

            boolean previousVersionDiffers = checkPreviousDiffers(atom.getContentGUID());
            if (previousVersionDiffers) {

                Collection<IGUID> previousVersion = new ArrayList<>();
                previousVersion.add(previous.getVersion().getVersionGUID());
                VersionBuilder versionBuilder = new VersionBuilder(atom.getContentGUID())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion);

                version = sos.addVersion(versionBuilder);

                this.previous = previous;
            } else {
                System.out.println("This create an identical new object to previous. Can be optimised to occupy less memory");
                this.previous = previous.getPreviousFILE();
            }

        } catch (StorageException | IOException |
                ManifestPersistException e) {
            throw new PersistenceException("SOS atom could not be created");
        } catch (ManifestNotMadeException e) {
            e.printStackTrace();
        }

        this.guid = getContentGUID();
    }

    @Override
    public IAttributes getAttributes() {

        // TODO - iterate over metadata and build attributes
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
        return 0;
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public void update(IData data) {
        // TODO - this should result in a new version pointing to a previous one

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
            // TODO
            //Compound compound = addAtomsInCompound(atoms);
            //builder = new VersionBuilder(compound.getContentGUID());
        }

        return retval;
    }

    private Compound addAtomsInCompound(Collection<Content> atoms) throws ManifestPersistException, ManifestNotMadeException {
        return sos.addCompound(CompoundType.DATA, atoms);
    }

    @Override
    public IData reify() {
        // LOG.log(LEVEL.INFO, "WEBDAV - SOSFile - Reify file with name " + name);

        // this will differ based on whether it is a single atom or a compound of atoms sos.getData(guid);
        // NOTE: idea - have a isChunked() method. If that method returns true, then reify returns data until null (no more chunks)

        InputStream stream = sos.getAtomContent(atom);
        IData data = new InputStreamData(stream, 4092); // FIXME - size of data expected should not be hardcoded

        return data;
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
