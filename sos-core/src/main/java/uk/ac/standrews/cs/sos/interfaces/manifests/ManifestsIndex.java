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
package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Versionable;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsIndex {

    /**
     *
     * @param manifest to track
     */
    void track(Manifest manifest);

    /**
     *
     * @param type of manifests to get
     * @return list of refs to manifests
     */
    Set<IGUID> getManifests(ManifestType type);

    /**
     * References to Versionable manifests (i.e. Version, Context)
     *
     * @param invariant of the asset
     * @return refs to versions of the asset
     */
    Set<IGUID> getVersions(IGUID invariant);

    /**
     * Tips of versionable manifest
     *
     * @param invariant of the asset
     * @return refs to tips
     * @throws TIPNotFoundException if the tip was not found
     */
    Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException;

    /**
     * Head of versionable manifest
     *
     * @param invariant of the asset
     * @return ref to head
     * @throws HEADNotFoundException if the head was not found
     */
    IGUID getHead(IGUID invariant) throws HEADNotFoundException;


    /**
     * Set this version to be the head
     *
     * @param versionable type of manifest
     */
    void setHead(Versionable versionable);

    /**
     * Advance the tip for this version
     *
     * @param versionable type of manifest
     */
    void advanceTip(Versionable versionable);

    /**
     * Remove references matching this manifest
     * @param manifest to be removed
     */
    void delete(Manifest manifest);

    /**
     * Persist index to disk
     */
    void flush();

    /**
     * TODO - this method should be implemented. Such methods should be elsewhere too!!!!
     * Rebuild index from disk
     */
    void rebuild();

    void clear();
}
