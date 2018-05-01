/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest.
 *
 * rest is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.rest.api;

import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;

/**
 * The following creates a node instance of the SOS.
 * THIS CLASS SHOULD BE USED FOR TESTING PURPOSES ONLY
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public SOSLocalNode sos;
    private LocalStorage localStorage;

    public void init(File file) {
        try {
            SettingsConfiguration settingsConfiguration = new SettingsConfiguration(file);
            startSOS(settingsConfiguration.getSettingsObj());

        } catch (SOSException | ConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void kill() throws DataStorageException {
        sos.kill(true);

        localStorage.destroy();
    }

    private void startSOS(SettingsConfiguration.Settings settings) throws SOSException {

        try {
            CastoreBuilder builder = settings.getStore().getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.settings(settings)
                .internalStorage(localStorage)
                .build();

    }

}
