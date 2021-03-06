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
package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.manifest.AbstractSignedManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetadataManifest extends AbstractSignedManifest implements Metadata {

    protected HashMap<String, Property> metadata;

    MetadataManifest(ManifestType manifestType, Role signer) {
        super(manifestType, signer);
    }

    MetadataManifest(ManifestType manifestType, IGUID signerRef) {
        super(manifestType, signerRef);
    }

    public MetadataManifest(HashMap<String, Property> metadata, Role signer) throws ManifestNotMadeException {
        this(ManifestType.METADATA, signer);

        this.metadata = metadata;
        this.guid = makeGUID();

        if (guid.isInvalid()) {
            throw new ManifestNotMadeException("Unable to make proper metadata manifest");
        }

        try {
            this.signature = makeSignature();
        } catch (SignatureException e) {
            throw new ManifestNotMadeException("Unable to sign the manifest");
        }

    }

    public MetadataManifest(IGUID guid, HashMap<String, Property> metadata, Role signer, String signature) {
        this(ManifestType.METADATA, signer);

        this.guid = guid;
        this.metadata = metadata;
        this.signature = signature;
    }

    public MetadataManifest(IGUID guid, HashMap<String, Property> metadata, IGUID signerRef, String signature) {
        this(ManifestType.METADATA, signerRef);

        this.guid = guid;
        this.metadata = metadata;
        this.signature = signature;
    }

    @Override
    public Property getProperty(String propertyName) {

        return metadata.get(propertyName);
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return metadata.containsKey(propertyName);
    }

    @Override
    public String[] getAllPropertyNames() {
        Set<String> keySet = metadata.keySet();

        return keySet.toArray(new String[keySet.size()]);
    }

    @Override
    public InputStream contentToHash() {

        String toHash = getType().toString() +
            "MP" + metadata.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(p -> p.getValue().toString())
                .collect(Collectors.joining("."));

        return IO.StringToInputStream(toHash);
    }

}
