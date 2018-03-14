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

import org.glassfish.jersey.test.JerseyTestNg;
import org.glassfish.jersey.test.TestProperties;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import javax.ws.rs.core.Application;
import java.io.File;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class CommonRESTTest extends JerseyTestNg.ContainerPerMethodTest {

    public static final String TEST_RESOURCES_PATH = "sos-rest/src/test/resources/";

    protected ServerState state;
    protected RESTConfig config;

    static {
        new SOS_LOG(GUIDFactory.generateRandomGUID(GUID_ALGORITHM));
    }

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        File configFile = new File(TEST_RESOURCES_PATH + "config.json");

        state = new ServerState();
        state.init(configFile);

        config.setSOS(state.sos);
    }

    @AfterMethod
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        state.kill();
    }

    @Override
    protected Application configure() {

        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);

        config = new RESTConfig();
        return config;
    }


//    @Override
//    public TestContainerFactory getTestContainerFactory() {
//        return new ExternalTestContainerFactory(){
//
//            @Override
//            public TestContainer create(URI baseUri, DeploymentContext context)
//                    throws IllegalArgumentException {
//                try {
//                    baseUri = new URI("http://localhost:9998/sos");
//                } catch (URISyntaxException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                return super.create(baseUri, context);
//            }
//        };
//    }

}
