package uk.ac.standrews.cs.sos.filesystem.utils;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    public static long UnixTimeToFileTime(long unixTime) {
        return unixTime * 1000L;
    }
}
