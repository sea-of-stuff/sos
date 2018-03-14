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
package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;

import java.io.IOException;
import java.io.InputStream;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * A manifest is an entity that describes assets, compounds and atoms by
 * recording metadata about them.
 * <p>
 * A manifest is not updatable.
 * <br>
 * Manifests are publishable within the sea of stuff and allow discoverability
 * of assets, compounds and atoms.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Manifest {

    /**
     * GUID representing this manifest
     *
     * @return the guid for this manifest
     */
    IGUID guid();

    /**
     * Get the type of manifest as a string.
     *
     * @return type of manifest as a string.
     */
    ManifestType getType();

    /**
     *
     * @return the content used to generate the GUID for this manifest
     */
    InputStream contentToHash() throws IOException;

    /**
     * Verifies that the GUID of this manifest matches its contents
     *
     * This method is different for the Atom Manifest, where we need to get the content from all the available locations
     *
     * @return true if the guid of the manifest matches its contents
     */
    default boolean verifyIntegrity() {

        try (InputStream contentToHash = contentToHash()) {
            if (contentToHash == null) {
                return false;
            }

            IGUID guidOfContent = GUIDFactory.generateGUID(GUID_ALGORITHM, contentToHash);
            if (guidOfContent.isInvalid() || !guid().equals(guidOfContent)) {
                return false;
            }

        } catch (IOException | GUIDGenerationException e) {
            return false;
        }

        return true;
    }

    /**
     * Check that the key-value pairs contained in the manifest comply to
     * the Sea of Stuff standard and are not malformed.
     * All required key-value pairs must be set in the manifest, for the latter
     * to be valid.
     *
     * @return true if the manifest is valid.
     */
    boolean isValid();

    int size();

}
