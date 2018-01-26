package uk.ac.standrews.cs.sos.impl.context;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextStats {

    public static class Predicate {

        AtomicLong pred_time_prep = new AtomicLong(0);
        AtomicLong pred_time_to_check_if_predicate_has_to_be_run = new AtomicLong(0);
        AtomicLong pred_time_to_run_predicate_on_current_dataset = new AtomicLong(0);
        AtomicLong pred_time_to_update_context = new AtomicLong(0);

        public AtomicLong getPred_time_prep() {
            return pred_time_prep;
        }

        public AtomicLong getPred_time_to_check_if_predicate_has_to_be_run() {
            return pred_time_to_check_if_predicate_has_to_be_run;
        }

        public AtomicLong getPred_time_to_run_predicate_on_current_dataset() {
            return pred_time_to_run_predicate_on_current_dataset;
        }

        public AtomicLong getPred_time_to_update_context() {
            return pred_time_to_update_context;
        }

    }

    public static class PolicyApply {

        AtomicLong policy_time_to_run_apply_on_current_dataset = new AtomicLong(0);

        public AtomicLong getPolicy_time_to_run_apply_on_current_dataset() {
            return policy_time_to_run_apply_on_current_dataset;
        }

    }

    public static class PolicyCheck {

        AtomicLong policy_time_to_run_check_on_current_dataset = new AtomicLong(0);

        public AtomicLong getPolicy_time_to_run_check_on_current_dataset() {
            return policy_time_to_run_check_on_current_dataset;
        }
    }
}
