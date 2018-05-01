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
package uk.ac.standrews.cs.sos;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import static uk.ac.standrews.cs.sos.constants.Paths.TEST_CONFIGURATIONS_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SetUpTest extends CommonTest {

    protected SOSLocalNode localSOSNode;
    protected LocalStorage localStorage;

    protected SettingsConfiguration.Settings settings;

    @Override
    @BeforeMethod
    public void setUp(Method testMethod) throws Exception {
        super.setUp(testMethod);

        // FORCE THE SOS PROTOCOL TO BE DE-REGISTERED. THIS STUFF IS HACKY, BUT THERE IS NOT ANOTHER WAY OF RESETTING THE
        // URLStreamHandlerFactory
        try {
            final Field factoryField = URL.class.getDeclaredField("factory");
            factoryField.setAccessible(true);
            factoryField.set(null, null);
            System.setProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled", "false");
        } catch (NoSuchFieldException | IllegalAccessException e1) {
            throw new Error("Could not access factory field on URL class: {}", e1);
        }

        createConfiguration();

        try {
            CastoreBuilder castoreBuilder = settings.getStore().getCastoreBuilder();
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        localSOSNode = builder.settings(settings)
                                .internalStorage(localStorage)
                                .build();
    }

    @AfterMethod
    public void tearDown() throws IOException, InterruptedException, DataStorageException {
        localSOSNode.kill(true);
        localStorage.destroy();
        FileUtils.deleteDirectory(new File("sos-core/src/test/resources/contexts/"));
    }

    protected void createConfiguration() throws ConfigurationException {
        File file = new File(TEST_CONFIGURATIONS_PATH + "config_setup.json");

        settings = new SettingsConfiguration(file).getSettingsObj();
    }
}
