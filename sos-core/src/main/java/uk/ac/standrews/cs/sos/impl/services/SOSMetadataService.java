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
package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.MetadataService;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSMetadataService implements MetadataService {

    private ManifestsDataService manifestsDataService;
    private MetadataEngine engine;

    public SOSMetadataService(MetadataEngine metadataEngine, ManifestsDataService manifestsDataService) {
        this.engine = metadataEngine;
        this.manifestsDataService = manifestsDataService;
    }

    @Override
    public Metadata processMetadata(MetadataBuilder metadataBuilder) throws MetadataException {

        return engine.processData(metadataBuilder);
    }

    @Override
    public void addMetadata(Metadata metadata) throws MetadataPersistException {

        try {
            manifestsDataService.addManifest(metadata);
        } catch (ManifestPersistException e) {
            throw new MetadataPersistException(e);
        }
    }

    @Override
    public Metadata getMetadata(IGUID guid) throws MetadataNotFoundException {

        try {
            return (Metadata) manifestsDataService.getManifest(guid, NodeType.MMS);
        } catch (ManifestNotFoundException e) {
            throw new MetadataNotFoundException("Unable to find metadata");
        }
    }

    @Override
    public Metadata getMetadata(NodesCollection nodesCollection, IGUID guid) throws MetadataNotFoundException {

        try {
            return (Metadata) manifestsDataService.getManifest(nodesCollection, NodeType.MMS, guid);
        } catch (ManifestNotFoundException e) {
            throw new MetadataNotFoundException("Unable to find metadata");
        }
    }

    @Override
    public void flush() {
        // DO NOTHING
    }

    @Override
    public void shutdown() {
        // DO NOTHING
    }
}
