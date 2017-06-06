package uk.ac.standrews.cs.sos.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Internals {

    public static final String CACHE_FILE = "manifests.cache";
    public static final String DDS_INDEX_FILE = "dds.index";
    public static final String CMS_INDEX_FILE = "cms.index";

    public static final int CACHE_FLUSHER_PERIOD = 600; // 10 minutes
    public static final TimeUnit CACHE_FLUSHER_TIME_UNIT = TimeUnit.SECONDS;

    private static final long ONE_KB = 1024L;
    private static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long CACHE_DATA_SIZE_LIMIT = 1L * ONE_MB;

    public static final String DEFAULT_USER_NAME = "DEFAULT_USER";
    public static final String DEFAULT_ROLE_NAME = "DEFAULT_ROLE";
    public static final String ACTIVE_USER = "ACTIVE_USER";
    public static final String ACTIVE_ROLE = "ACTIVE_ROLE";
}
