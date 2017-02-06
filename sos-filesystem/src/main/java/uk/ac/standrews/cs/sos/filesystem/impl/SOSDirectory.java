package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.fs.exceptions.AccessFailureException;
import uk.ac.standrews.cs.fs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributedStatefulObject;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.builders.AssetBuilder;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utils.Error;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDirectory extends SOSFileSystemObject implements IDirectory {

    private Set<Content> contents;
    private Compound compound;

    public SOSDirectory(Agent sos, SOSDirectory parent, String name) throws GUIDGenerationException {
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

    public SOSDirectory(Agent sos, Asset asset, Compound compound) {
        super(sos);

        contents = compound.getContents();
        this.compound = compound;
        this.asset = asset;

        this.guid = asset.guid();
    }

    // Use this constructor for the root folder only
    public SOSDirectory(Agent sos, Asset asset) {
        super(sos);
        this.asset = asset;
        this.name = null;
        this.guid = asset.guid();

        try {
            compound = (Compound) sos.getManifest(asset.getContentGUID());
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
            contents = new LinkedHashSet<>(previous.getContents());
            addOrUpdate(name, new Content(name, object.getGUID()));

            compound = sos.addCompound(CompoundType.COLLECTION, contents);

            boolean previousVersionDiffers = previousAssetDiffers(compound.getContentGUID());
            if (previousVersionDiffers) {

                Set<IGUID> previousVersion = new LinkedHashSet<>();
                previousVersion.add(previous.getAsset().getVersionGUID());
                AssetBuilder assetBuilder = new AssetBuilder(compound.getContentGUID())
                        .setInvariant(previous.getInvariant())
                        .setPrevious(previousVersion);

                asset = sos.addAsset(assetBuilder);

                this.guid = asset.guid();
                this.previous = previous;
            } else {
                System.out.println("This create an identical new object to previous. Can be optimised to occupy less memory?");
                this.previous = previous.getPreviousDIR();
            }

        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }

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
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Add file " + name + " to folder " + this.name);

        addObject(name, file, null);
    }

    @Override
    public void addDirectory(String name, IDirectory directory) throws BindingPresentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Add directory " + name + " to folder " + this.name);

        addObject(name, directory, null);
    }

    @Override
    public void remove(String name) throws BindingAbsentException {
        SOS_LOG.log(LEVEL.INFO, "WEBDAV - Remove object " + name + " from folder " + this.name);

        Content contentToRemove = getContent(name);
        contents.remove(contentToRemove);

        try {
            compound = sos.addCompound(CompoundType.COLLECTION, contents);
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
        }

        this.guid = getContentGUID();
    }

    protected IGUID getContentGUID() {
        return compound.getContentGUID();
    }

    @Override
    public Iterator iterator() {
        // iterate over elements of the compound
        return new CompoundIterator();
    }

    private Set<Content> getContents() {
        return contents;
    }

    @Override
    public IAttributes getAttributes() {
        return null;
    }

    @Override
    public void setAttributes(IAttributes attributes) {}

    @Override
    public long getCreationTime() throws AccessFailureException {
        return 0;
    }

    @Override
    public long getModificationTime() throws AccessFailureException {
        return 0;
    }

    private SOSFileSystemObject getObject(IGUID guid) {
        if (guid == null || guid.isInvalid())
            return null;

        try {
            Manifest manifest = sos.getManifest(guid);
            if (manifest instanceof Asset) {
                return getObject((Asset) manifest);
            } else {
                SOS_LOG.log(LEVEL.ERROR, "WEBDAV - attempting to retrieve manifest of wrong type. " +
                        "GUID: " + guid + " Type: " + manifest.getManifestType());
            }
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSFileSystemObject getObject(Asset asset) {

        try {
            IGUID contentGUID = asset.getContentGUID();
            Manifest manifest = sos.getManifest(contentGUID);
            if (manifest instanceof Atom) {
                return new SOSFile(sos, asset);

            } else if (manifest instanceof  Compound) {
                return getCompoundObject(asset, (Compound) manifest);
            }
        } catch (GUIDGenerationException | ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSDirectory getCompoundObject(Asset asset, Compound compound) throws GUIDGenerationException {
        // Still this might be a data compound
        if (compound.getType() == CompoundType.DATA) {
            return null; // Make compound file
        } else {
            return new SOSDirectory(sos, asset, compound);
        }
    }

    public IDirectory getParent() {
        return parent;
    }

    @Override
    public void setParent(IDirectory parent) {
        this.parent = (SOSDirectory) parent;
    }

    private SOSDirectory getPreviousDIR() {
        return (SOSDirectory) previous;
    }

    private void addObject(String name, IAttributedStatefulObject object, IAttributes atts) throws BindingPresentException {
        if (contains(name)) {
            throw new BindingPresentException("Object already exists");
        } else {
            contents.add(new Content(name, object.getGUID()));
        }
    }

    private Content getContent(String name) {
        for(Content content:contents) {
            if (content.getLabel().equals(name)) {
                return content;
            }
        }
        return null;
    }

    private void addOrUpdate(String name, Content newContent) {
        for(Content content:contents) {
            if (content.getLabel().equals(name)) {
                contents.remove(content);
                contents.add(newContent);

                return;
            }
        }

        contents.add(newContent);
    }

    /**
     * Iterator for the compound. The iterator returns Contents until hasNext returns false.
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
