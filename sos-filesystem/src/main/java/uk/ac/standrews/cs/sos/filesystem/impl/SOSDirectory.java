/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module filesystem.
 *
 * filesystem is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * filesystem is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with filesystem. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.filesystem.impl;

import uk.ac.standrews.cs.fs.exceptions.BindingAbsentException;
import uk.ac.standrews.cs.fs.exceptions.BindingPresentException;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystemObject;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.fs.persistence.interfaces.IAttributes;
import uk.ac.standrews.cs.fs.util.Attributes;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.filesystem.SOSFileSystemException;
import uk.ac.standrews.cs.sos.filesystem.utils.FileSystemConstants;
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

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
     * @throws SOSFileSystemException
     */
    public SOSDirectory(Agent sos, String name, SOSDirectory parent) throws SOSFileSystemException {
        super(sos);

        try {
            this.name = name;
            this.parent = parent;
            contents = new HashSet<>();

            CompoundBuilder compoundBuilder = new CompoundBuilder()
                    .setType(CompoundType.COLLECTION)
                    .setContents(contents);


            // FIXME - previous, and metadata
            VersionBuilder versionBuilder = new VersionBuilder().setCompoundBuilder(compoundBuilder);
            sos.addCollection(versionBuilder);

        } catch (ServiceException e) {
            throw new SOSFileSystemException();
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
            compound = (Compound) sos.getManifest(version.content());
            contents = compound.getContents();
        } catch (ServiceException e) {
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
            CompoundBuilder compoundBuilder = new CompoundBuilder()
                    .setType(CompoundType.COLLECTION)
                    .setContents(contents);

            this.compound = sos.addCompound(compoundBuilder);

            boolean previousVersionDiffers = previousAssetDiffers(compound.guid());
            if (previousVersionDiffers) {

                Set<IGUID> previousVersion = new LinkedHashSet<>();
                previousVersion.add(previous.getVersion().version());
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

        } catch (ServiceException e) {
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
            CompoundBuilder compoundBuilder = new CompoundBuilder()
                    .setType(CompoundType.COLLECTION)
                    .setContents(contents);
            this.compound = sos.addCompound(compoundBuilder);

            boolean previousVersionDiffers = previousAssetDiffers(compound.guid());
            if (previousVersionDiffers) {

                Set<IGUID> previousVersion = new LinkedHashSet<>();
                previousVersion.add(previous.getVersion().version());
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

        } catch (ServiceException e) {
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
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        return null;
    }

    private SOSFileSystemObject getObject(Version version) {

        try {
            IGUID contentGUID = version.content();
            Manifest manifest = sos.getManifest(contentGUID);
            if (manifest instanceof Atom) {
                return new SOSFile(sos, version);

            } else if (manifest instanceof  Compound) {
                return getCompoundObject(version, (Compound) manifest);
            }
        } catch (GUIDGenerationException | ServiceException e) {
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
            ErrorHandling.hardError("unimplemented method");
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
