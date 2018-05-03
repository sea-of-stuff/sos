/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module slave.
 *
 * slave is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * slave is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with slave. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.slave;

import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.jetty.JettyApp;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO - handle clean exit
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Main {

    public static void main(String[] args) throws SOSException, ConfigurationException {

        File configFile = new File(args[0]);
        SettingsConfiguration configuration = new SettingsConfiguration(configFile);

        SOSLocalNode node = startSOS(configuration.getSettingsObj());

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            try {
                SOS_LOG.log(LEVEL.INFO, "Launching the REST App on port: " + node.getHostAddress().getPort());
                JettyApp.RUN(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static SOSLocalNode startSOS(SettingsConfiguration.Settings settings) throws SOSException {

        LocalStorage localStorage;
        try {
            CastoreBuilder builder = settings.getStore().getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        return builder.settings(settings)
                .internalStorage(localStorage)
                .build();
    }

}
