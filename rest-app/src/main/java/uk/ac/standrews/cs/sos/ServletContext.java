package uk.ac.standrews.cs.sos;

import com.google.gson.GsonBuilder;
import uk.ac.standrews.cs.sos.deserializers.ContentDeserializer;
import uk.ac.standrews.cs.sos.deserializers.LocationBundleDeserializer;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServletContext implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("INIT CONTEXT");

        System.out.println("Starting SOS");
        ServerState.startSOS();
        System.out.println("SOS started");
        configureGson();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private static void configureGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        registerGSonTypeAdapters(gsonBuilder);
        ServerState.gson = gsonBuilder.create();
    }

    private static void registerGSonTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(LocationBundle.class, new LocationBundleDeserializer());
        builder.registerTypeAdapter(Content.class, new ContentDeserializer());
    }
}
