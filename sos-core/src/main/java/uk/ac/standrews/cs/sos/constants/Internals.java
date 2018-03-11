package uk.ac.standrews.cs.sos.constants;

import uk.ac.standrews.cs.guid.ALGORITHM;

import java.util.concurrent.TimeUnit;

import static uk.ac.standrews.cs.guid.ALGORITHM.SHA256;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Internals {

    public static ALGORITHM GUID_ALGORITHM = SHA256;

    public static final String MANIFESTS_CACHE_FILE = "manifests.cache";
    public static final String MANIFESTS_INDEX_FILE = "manifests.index";
    public static final String MDS_INDEX_FILE = "mds.index";
    public static final String CMS_INDEX_FILE = "cms.index";
    public static final String USRO_INDEX_FILE = "usro.index";
    public static final String LOCATIONS_INDEX_FILE = "locations.index";
    public static final String DB_FILE = "node.db";

    public static final TimeUnit NODE_MAINTAINER_TIME_UNIT = TimeUnit.SECONDS;

    public static final String DEFAULT_USER_NAME = "DEFAULT_USER";
    public static final String DEFAULT_ROLE_NAME = "DEFAULT_ROLE";

    /**
     * Use this multiplier to make sure that the NDS attempts to return about 3 times as many nodes as requested for a LIMITED nodes' request
     */
    public static final int REPLICATION_FACTOR_MULTIPLIER = 3;

    public static final int TIMEOUT_LIMIT_S = 30;
}
