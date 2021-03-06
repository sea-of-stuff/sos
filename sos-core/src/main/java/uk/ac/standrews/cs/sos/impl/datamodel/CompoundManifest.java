/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.impl.datamodel;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A compound is an immutable collection of (references to)
 * atoms or other compounds (contents).
 * Compounds do not contain data - they refer to data - and are identified by
 * GUID (derived from their contents).
 *
 * <p>
 * Intuition: <br>
 * Compounds are provided to permit related atoms and compounds to be gathered
 * together (think of folders, zip files, packages etc. without containment).
 * <p>
 * A compound can be used for de-duplication. Two collections of data
 * (atoms and compounds) might contain the same content. The data does not have
 * to be duplicated for each compound, since we can uniquely refer to the data
 * from the compound itself.
 *
 * <br>
 * Manifest describing a Compound.
 * <p>
 * Manifest - GUID <br>
 * ManifestType - COMPOUND <br>
 * Signature - signature of the manifest <br>
 * ContentGUID - guid of the compound content <br>
 * Contents - contents of this compound
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CompoundManifest extends AbstractSignedManifest implements Compound {

    protected Set<Content> contents;
    protected CompoundType type;

    // This constructor is needed for SecureCompoundManifest
    CompoundManifest(ManifestType type, CompoundType compoundType, Role signer) {
        super(type, signer);

        this.type = compoundType;
    }

    /**
     * Creates a valid compound manifest given a collection of contents and an
     * identity to sign the manifest.
     *
     * @param contents of the compound
     * @param signer to sign the compound
     * @throws ManifestNotMadeException if the compound could not be made
     */
    public CompoundManifest(CompoundType type, Set<Content> contents, Role signer) throws ManifestNotMadeException {
        this(ManifestType.COMPOUND, type, signer);

        this.contents = contents;
        this.guid = makeGUID();

        if (guid.isInvalid()) {
            throw new ManifestNotMadeException("Failed to generate content GUID");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }

    }

    public CompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, Role signer, String signature) {
        super(ManifestType.COMPOUND, signer);

        assert(type != null);

        this.type = type;
        this.guid = contentGUID;
        this.contents = contents;
        this.signature = signature;
    }

    public CompoundManifest(CompoundType type, IGUID contentGUID, Set<Content> contents, IGUID signerRef, String signature) {
        super(ManifestType.COMPOUND, signerRef);

        assert(type != null);

        this.type = type;
        this.guid = contentGUID;
        this.contents = contents;
        this.signature = signature;
    }

    @Override
    public Set<Content> getContents() {
        return contents;
    }

    @Override
    public Content getContent(String label) {

        for(Content content:contents) {
            if (content.getLabel().equals(label)) {
                return content;
            }
        }

        return null; // TODO - throw exception
    }

    @Override
    public Content getContent(IGUID guid) {

        for(Content content:contents) {
            if (content.getGUID().equals(guid)) {
                return content;
            }
        }

        return null; // TODO - throw exception
    }

    @Override
    public CompoundType getCompoundType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return super.isValid() &&
                type != null &&
                contents != null &&
                isGUIDValid(guid);
    }

    @Override
    public InputStream contentToHash() {

        String toHash = getType() +
                "T" + getCompoundType() +
                "C" + contents.stream()
                    .sorted(Comparator.comparing(c -> c.getGUID().toMultiHash()))
                    .map(Object::toString)
                    .collect(Collectors.joining("."));

        return IO.StringToInputStream(toHash);
    }

}
