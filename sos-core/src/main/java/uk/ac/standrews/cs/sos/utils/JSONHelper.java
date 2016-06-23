package uk.ac.standrews.cs.sos.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class JSONHelper {

    private static ObjectMapper mapper;

    public static ObjectMapper JsonObjMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }
}
