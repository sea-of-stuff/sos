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
import java.util.Optional;
import java.util.Properties;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VelocityCustomEngine extends TemplateEngine {

        private final VelocityEngine velocityEngine;
        private String encoding;

        /**
         * Constructor
         */
        public VelocityCustomEngine() {
            Properties properties = new Properties();
            properties.setProperty("resource.loader", "class");
            properties.setProperty(
                    "class.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            properties.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());

            this.velocityEngine = new org.apache.velocity.app.VelocityEngine(properties);
        }

        /**
         * Constructor
         *
         * @param encoding The encoding to use
         */
        public VelocityCustomEngine(String encoding) {
            this();
            this.encoding = encoding;
        }

        /**
         * Constructor
         *
         * @param velocityEngine The velocity engine, must not be null.
         */
        public VelocityCustomEngine(VelocityEngine velocityEngine) {
            if (velocityEngine == null) {
                throw new IllegalArgumentException("velocityEngine must not be null");
            }
            this.velocityEngine = velocityEngine;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String render(ModelAndView modelAndView) {
            String templateEncoding = Optional.ofNullable(this.encoding).orElse(StandardCharsets.UTF_8.name());
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