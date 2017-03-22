package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.model.*;
import uk.ac.standrews.cs.sos.model.manifests.ContentImpl;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utils.Error;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SOSDirectory consists of an asset pointing to a compound of contents.
 * The asset manifest is created when persisting the object or at the constructor level if
 * the SOSDirectory has a previous one
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {

    private Set<Content> contents;
    private Compound compound;

    /**
     * Creates an empty SOSDirectory with a given name
     * @param sos reference to the SOS
     * @param name of the directory
     * @param parent of the directory
     * @throws GUIDGenerationException
     */
    public SOSDirectory(Agent sos, String name, SOSDirectory parent) throws GUIDGenerationException {
        super(sos);
        this.name = name;
        this.parent = parent;
        contents = new HashSet<>();

        try {
            compound = sos.addCompound(CompoundType.COLLECTION, contents);
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }
    }

    /**
     * This constructor creates an SOSDirectory object from an already existing asset
     *
     * @param sos
     * @param version
     */
    public SOSDirectory(Agent sos, Version version) {
        super(sos);
        this.name = null;
        this.version = version;
        this.guid = version.guid();

        try {
            compound = (Compound) sos.getManifest(version.getContentGUID());
            contents = compound.getContents();
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a directory object containing a named object
     *
     * If the directory already contains an element with the same name, then the old object will be replaced with the new one
     *
     * @param sos
     * @param previous previous version of this directory
     * @param name of the object
     * @param object object contained in this directory
     */
    public SOSDirectory(Agent sos, SOSDirectory previous, String name, SOSFileSystemObject object) {
        super(sos);

        try {
            this.name = previous.name;

            this.contents = new LinkedHashSet<>(previous.getContents());
            addOrUpdate(new ContentImpl(name, object.getGUID()));
            this.compound = sos.addCompound(CompoundType.COLLECTION, contents);

            boolean previousVersionDiffers = previousAssetDiffers(compound.guid());
            if (previousVersionDiffers) {

                Set<IGUID> previousVersion = new LinkedHashSet<>();
                previousVersion.add(previous.getVersion().getVersionGUID());
                VersionBuilder versionBuilder = new VersionBuilder(compound.guid())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion);

                this.version = sos.addVersion(versionBuilder);
                this.guid = version.guid();
                this.previous = previous;
            } else {
                System.err.println("This create an identical new object to previous. Can be optimised to occupy less memory?");
                this.previous = previous.getPreviousObject();
            }

        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }

    }

    /**
     * Build an SOSDirectory without the named object
     *
     * @param sos a reference to the SOS
     * @param previous directory for this new directory
     * @param name of the object to be removed from previous to make the new directory
     */
    public SOSDirectory(Agent sos, SOSDirectory previous, String name) {
        super(sos);

        try {
            this.name = previous.name;

            this.contents = new LinkedHashSet<>(previous.getContents());
            removeContent(name);

            // TODO - the code below is the same as in the other constructor for this class
            this.compound = sos.addCompound(CompoundType.COLLECTION, contents);

            boolean previousVersionDiffers = previousAssetDiffers(compound.guid());
            if (previousVersionDiffers) {

                Set<IGUID> previousVersion = new LinkedHashSet<>();
                previousVersion.add(previous.getVersion().getVersionGUID());
                VersionBuilder versionBuilder = new VersionBuilder(compound.guid())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion);

                this.version = sos.addVersion(versionBuilder);
                this.guid = version.guid();
                this.previous = previous;
            } else {
                System.err.println("This create an identical new object to previous. Can be optimised to occupy less memory?");
                this.previous = previous.getPreviousObject();
            }

        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IAttributes getAttributes() {

        IAttributes dummyAttributes = new Attributes(FileSystemConstants.ISDIRECTORY + Attributes.EQUALS + "true" + Attributes.SEPARATOR);
        return dummyAttributes;
    }

    @Override
    public IFileSystemObject get(String name) {
        Content content = getContent(name);
        IGUID guid = null;
        if (content != null) {
            guid = content.getGUID();
        }

        return getObject(guid);
    }

    @Override
    public boolean contains(String name) {
        return get(name) != null;
    }

    @Override
    public void addFile(String name, IFile file, String content_type) throws BindingPresentException {
        // We do not add files to immutable entities
    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
        // We do not add directories to immutable entities
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        // This method is not implemented since we do not actually remove content from a directory.
        // Instead, we build a new directory without that content
    }

    protected IGUID getContentGUID() {
        return compound.guid();
    }

    @Override
    public Iterator iterator() {
        // iterate over elements of the compound
        return new CompoundIterator();
    }

    private Set<Content> getContents() {
        return contents;
    }

    private SOSFileSystemObject getObject(IGUID guid) {
        if (guid == null || guid.isInvalid())
            return null;

        try {
            Manifest manifest = sos.getManifest(guid);
            if (manifest instanceof Version) {
                return getObject((Version) manifest);
            } else {
                SOS_LOG.log(LEVEL.ERROR, "WEBDAV - attempting to retrieve manifest of wrong type. " +
                        "GUID: " + guid + " Type: " + manifest.getType());
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSFileSystemObject getObject(Version version) {

        try {
            IGUID contentGUID = version.getContentGUID();
            Manifest manifest = sos.getManifest(contentGUID);
            if (manifest instanceof Atom) {
                return new SOSFile(sos, version);

            } else if (manifest instanceof  Compound) {
                return getCompoundObject(version, (Compound) manifest);
            }
        } catch (GUIDGenerationException | ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSDirectory getCompoundObject(Version version, Compound compound) throws GUIDGenerationException {
        // Still this might be a data compound
        if (compound.getCompoundType() == CompoundType.DATA) {
            return null; // TODO - Make compound file SOSFile(type compound), etc
        } else {
            return new SOSDirectory(sos, version);
        }
    }

    /**
     * Get the content matching the specified name
     * @param name
     * @return
     */
    private Content getContent(String name) {
        for(Content content:contents) {
            if (content.getLabel().equals(name)) {
                return content;
            }
        }
        return null;
    }

    /**
     * Remove the content matching the specified name
     * @param name
     */
    private void removeContent(String name) {

        Content content = getContent(name);
        if (content != null) {
            contents.remove(content);
        }
    }

    /**
     * Add the new content with the given name. If there is already a content
     * with that name, remove it and replace
     * @param content
     */
    private void addOrUpdate(ContentImpl content) {
        removeContent(content.getLabel());
        contents.add(content);
    }

    /**
     * Iterator for the contents of the compound. The iterator returns Contents until hasNext returns false.
     */
    private class CompoundIterator implements Iterator {

        Iterator<Content> contentIterator;

        CompoundIterator() {
            contentIterator = contents.iterator();
        }

        public void remove() {
            Error.hardError("unimplemented method");
        }

        public boolean hasNext() {
            return contentIterator.hasNext();
        }

        public Object next() {
            Content content = contentIterator.next();

            SOSFileSystemObject obj = getObject(content.getGUID());
            String name = content.getLabel();
            if (obj == null) {
                return null;
            } else {
                return new NameAttributedPersistentObjectBinding(name, obj);
            }
        }
    }

}
