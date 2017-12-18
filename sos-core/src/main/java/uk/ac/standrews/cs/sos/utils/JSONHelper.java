package uk.ac.standrews.cs.sos.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON Helper (it uses Jackson)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JSONHelper {

    private static ObjectMapper mapper;

    public static ObjectMapper jsonObjMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper()
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                    .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                    .enable(SerializationFeature.INDENT_OUTPUT);
        }
        return mapper;
    }
}
