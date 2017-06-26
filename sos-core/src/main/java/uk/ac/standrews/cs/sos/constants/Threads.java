package uk.ac.standrews.cs.sos.constants;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Threads {

    public static final int CACHE_FLUSHER_PS = 1; // PS stands for POOL_SIZE
    public static final int TASKS_SCHEDULER_PS = 4;
    public static final int CMS_SCHEDULER_PS = 4;

    // Context Service
    public static final int PREDICATE_PERIODIC_INIT_DELAY_S = 30;
    public static final int PREDICATE_PERIODIC_DELAY_S = 60;
    public static final int POLICIES_PERIODIC_INIT_DELAY_S = 45;
    public static final int POLICIES_PERIODIC_DELAY_S = 60;
    public static final int POLICIES_CHECK_PERIODIC_INIT_DELAY_S = 45;
    public static final int POLICIES_CHECK_PERIODIC_DELAY_S = 60;
    public static final int GET_DATA_PERIODIC_INIT_DELAY_S = 60;
    public static final int GET_DATA_PERIODIC_DELAY_S = 60;
    public static final int SPAWN_PERIODIC_INIT_DELAY_S = 60;
    public static final int SPAWN_PERIODIC_DELAY_S = 60;

}
