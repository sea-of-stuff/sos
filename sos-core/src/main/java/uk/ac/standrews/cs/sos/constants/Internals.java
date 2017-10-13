package uk.ac.standrews.cs.sos.constants;

import java.util.concurrent.TimeUnit;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Internals {

    public static final String CACHE_FILE = "manifests.cache";
    public static final String INDEX_FILE = "manifests.index";
    public static final String DDS_INDEX_FILE = "dds.index";
    public static final String CMS_INDEX_FILE = "cms.index";
    public static final String USRO_CACHE_FILE = "usro.cache";
    public static final String LOCATIONS_INDEX_FILE = "locations.index";

    public static final TimeUnit NODE_MAINTAINER_TIME_UNIT = TimeUnit.SECONDS;

    public static final String DEFAULT_USER_NAME = "DEFAULT_USER";
    public static final String DEFAULT_ROLE_NAME = "DEFAULT_ROLE";
    public static final String ACTIVE_USER = "ACTIVE_USER";
    public static final String ACTIVE_ROLE = "ACTIVE_ROLE";

    /**
     * Use this multiplier to make sure that the NDS attempts to return about 3 times as many nodes as requested for a LIMITED nodes' request
     */
    public static final int REPLICATION_FACTOR_MULTIPLIER = 3;
}
