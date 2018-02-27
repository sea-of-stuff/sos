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