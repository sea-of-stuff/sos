package uk.ac.standrews.cs.sos.web;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VelocityUtils {

    public static String RenderTemplate(String template, Map model) {
        return new VelocityTemplateEngine().render(new ModelAndView(model, template));
    }

    public static String RenderTemplate(String template) {
        return new VelocityTemplateEngine().render(new ModelAndView(new HashMap<>(),template));
    }

}
