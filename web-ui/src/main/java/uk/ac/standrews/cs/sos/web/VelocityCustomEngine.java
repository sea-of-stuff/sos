/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module web-ui.
 *
 * web-ui is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * web-ui is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with web-ui. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.web;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import spark.ModelAndView;
import spark.TemplateEngine;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VelocityCustomEngine extends TemplateEngine {

        private final VelocityEngine velocityEngine;

        /**
         * Constructor
         */
        VelocityCustomEngine() {
            Properties properties = new Properties();
            properties.setProperty("resource.loader", "class");
            properties.setProperty(
                    "class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            properties.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
            properties.setProperty("runtime.log", "logs/velocity.log");

            this.velocityEngine = new VelocityEngine(properties);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String render(ModelAndView modelAndView) {
            String templateEncoding = StandardCharsets.UTF_8.name();
            Template template = velocityEngine.getTemplate(modelAndView.getViewName(), templateEncoding);
            Object model = modelAndView.getModel();
            if (model instanceof Map) {
                Map<?, ?> modelMap = (Map<?, ?>) model;
                VelocityContext context = new VelocityContext(modelMap);
                StringWriter writer = new StringWriter();
                template.merge(context, writer);
                return writer.toString();
            } else {
                throw new IllegalArgumentException("modelAndView must be of type java.util.Map");
            }
        }

    }