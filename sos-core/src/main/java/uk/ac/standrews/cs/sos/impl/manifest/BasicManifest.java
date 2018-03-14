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
package uk.ac.standrews.cs.sos.impl.manifest;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.constants.Internals;
import uk.ac.standrews.cs.sos.instrument.InstrumentFactory;
import uk.ac.standrews.cs.sos.instrument.StatsTYPE;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * The BasicManifest defines the base implementation for all other manifests.
 * This class implements some of the methods that can be generalised across all
 * other types of manifests. Manifests extending the BasicManifest MUST provide
 * implementations for the abstract methods defined in this class.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasicManifest implements Manifest {

    private int size = -1;

    protected IGUID guid;
    protected ManifestType manifestType;

    /**
     * Constructor for a BasicManifest.
     * Initialise the type of manifest.
     *
     * @param manifestType type of manifest
     */
    protected BasicManifest(ManifestType manifestType) {
        this.manifestType = manifestType;
    }

    /**
     * Gets the type of this manifest.
     *
     * @return the type of this manifest.
     */
    @Override
    public ManifestType getType() {
        return this.manifestType;
    }

    @Override
    public abstract InputStream contentToHash() throws IOException;

    /**
     * Checks whether this manifest contains valid key-value entries.
     *
     * @return true if the manifest is valid.
     */
    @Override
    public boolean isValid() {
        return hasManifestType();
    }

    @Override
    public int size() {

        if (size == -1) {
            size = this.toString().length();
        }

        return size;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    /**
     * Checks if the given GUID contains valid hex characters.
     *
     * @param guid to validated.
     * @return true if the guid is valid.
     */
    protected boolean isGUIDValid(IGUID guid) {
        return guid != null && !guid.isInvalid();
    }

    private boolean hasManifestType() {
        return manifestType != null;
    }

    /**
     * Transform this object into a JSON string
     *
     * @return representation of manifest in JSON string
     */
    @Override
    public String toString() {
        try {
            return JSONHelper.jsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to generate JSON for manifest object " + guid());
            return "";
        }

    }

    protected String getCollectionToHashOrSign(Set<IGUID> collection) {

        return collection.stream()
                .sorted(Comparator.comparing(IGUID::toMultiHash))
                .map(IKey::toMultiHash)
                .collect(Collectors.joining("."));
    }

    protected IGUID makeGUID() {

        try (Data data = new InputStreamData(contentToHash())){

            long start = System.nanoTime();
            IGUID guid = GUIDFactory.generateGUID(GUID_ALGORITHM, data.getInputStream());
            long duration = System.nanoTime() - start;

            StatsTYPE subtype = StatsTYPE.getHashType(Internals.GUID_ALGORITHM);
            InstrumentFactory.instance().measure(StatsTYPE.guid_manifest, subtype, Long.toString(data.getSize()), duration);

            return guid;
        } catch (GUIDGenerationException | IOException e) {
            return new InvalidID();
        }

    }

}
