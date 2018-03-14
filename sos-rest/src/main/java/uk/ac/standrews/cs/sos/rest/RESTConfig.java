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
package uk.ac.standrews.cs.sos.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.rest.filters.*;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTConfig extends ResourceConfig {

    private static final String REST_PACKAGE = "uk.ac.standrews.cs.sos.rest.api";

    public static SOSLocalNode sos;

    public RESTConfig() {
        packages(REST_PACKAGE);

        register(LoggingFeature.class);
        register(JacksonProvider.class);
        register(JacksonFeature.class);

        register(GeneralFilter.class);
        register(StorageFilter.class);
        register(NDSFilter.class);
        register(MDSFilter.class);
        register(CMSFilter.class);
        register(RMSFilter.class);
        register(MMSFilter.class);
        register(ExperimentFilter.class);
    }

    public void setSOS(SOSLocalNode sos) {
        RESTConfig.sos = sos;
    }

    @Provider
    public static class JacksonProvider implements ContextResolver<ObjectMapper> {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        static {
            MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
            MAPPER.disable(MapperFeature.USE_GETTERS_AS_SETTERS);
            MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        }

        public JacksonProvider() {
            System.out.println("Instantiate JacksonProvider");
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return MAPPER;
        }
    }
}
