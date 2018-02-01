#######
# SOS EXPERIMENTS
# Plots display means with confidence intervals (.95 confidence level)
########

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/os_background.r")
source("r_scripts/exp_basic.r")
source("r_scripts/exp_nb.r")
source("r_scripts/exp_repl.r")
source("r_scripts/exp_pr_1.r")
source("r_scripts/exp_po_1.r")
source("r_scripts/exp_po_3.r")
source("r_scripts/exp_co.r")
source("r_scripts/exp_do.r")

############
# CPU/MEM
############
mem("output/io_1__2017_11_13T17_09_13_062Z_os.tsv")
cpu("output/io_1__2017_11_13T17_09_13_062Z_os.tsv")
cpu("output/io_1__2017_11_13T17_09_13_062Z_os.tsv", barplot = FALSE)

mem("output/io_1_on_text1mb_10its_1_os.tsv")
cpu("output/io_1_on_text1mb_10its_1_os.tsv")


############
# IO
############

# Read the CVS file
# Dataset: text_100kb
# Iterations: 10
io("output/io_1__2017_11_13T17_09_13_062Z.tsv", "plot title") # With cache invalidation
io("output/io_1__2017_11_13T17_19_29_095Z.tsv", "plot title") # Without cache invalidation

# TODO - with and without cache invalidation
io("remote/io_2_run_3.tsv", "IO performance.", ratio=FALSE)
io("remote/io_2_run_3.tsv", "IO performance.", ratio=TRUE)

mem("remote/io_2_run_3_os.tsv")
cpu("remote/io_2_run_3_os.tsv")

############
# GUID
############

guid_data("remote/guid_2_run_3.tsv", "Hash Algorithms Performance. Data: from 0 to 100mb.", showSummary = FALSE, ratio=FALSE)
guid_data("remote/guid_2_run_3.tsv", "Hash Algorithms Performance. Data: from 0 to 100mb.", showSummary = FALSE, ratio=TRUE)

mem("remote/guid_2_run_3.tsv")
cpu("remote/guid_2_run_3.tsv")

##############
# NB_x
##############

nb("output/nb_1_test3.tsv", titlePlot="Normal Behaviour exp.")

##############
# REPL_x
##############

repl("remote/repl_1_test4.tsv", subtype="replicate_atom", yMax=4.25, titlePlot="Data replication (Dataset: 100KB)");

repl("remote/repl_2_test1.tsv", subtype="replicate_manifest", yMax=.8, titlePlot="Manifest replication (100 Version manifests)");

############
# PR_1
############

# Number of iterations: 100.
pr_1("remote/text_100kb_100its.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset")

# Number of iterations: 20.
pr_1("remote/pr_1_text100kb_20its.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset")

##############
# PO_A_1
##############

# Number of iterations: 10
po_1("remote/po_a_1_text100kb_10its.tsv", type="policies", subtype="policy_apply_dataset", 
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset", yMax=2)
po_1("remote/po_a_1_text100kb_10its.tsv", type="policies", subtype="policy_apply_dataset", 
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset", showSummary = TRUE)


po_1("remote/po_a_1_text_100kb_10its_with_scp_7.tsv", type="policies", subtype="policy_apply_dataset", 
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset", yMax=100)

##############
# PO_A_3
##############

# Number of iterations: 10
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", yMax=10)
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", showSummary=TRUE)


##############
# PO_C_1
##############

# Number of iterations: 10
po_1("remote/po_c_1_100kb_10its_2.tsv", type="checkPolicies", subtype="policy_check_dataset", 
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset", yMax=2)

##############
# PO_C_3
##############

# Number of iterations: 10
po_3("remote/po_c_3_text100kb_its10_1.tsv", type="checkPolicies", titlePlot = "Time to run multiple policy apply functions over ???", yMax=3.5)


##############
# CO_x
##############

co("remote/co_a_test25.tsv", type="policies", subtype="policy_apply_dataset", yMax=5, titlePlot="CO_A_1 (10 iterations), 100kb dataset. Replication factor = 1")
co("remote/co_a_2_v1.tsv", type="policies", subtype="policy_apply_dataset", yMax=10, titlePlot="CO_A_2 (10 iterations), 100kb dataset. Replication factor = 1-10")
co("remote/co_c_1_v1.tsv", type="checkPolicies", subtype="policy_check_dataset", yMax=10, titlePlot="CO_C_1 (10 iterations), 100kb dataset. Replication factor = 1")
co("remote/co_c_2_v1.tsv", type="checkPolicies", subtype="policy_check_dataset", yMax=10, titlePlot="CO_C_2 (10 iterations), 100kb dataset. Replication factor = 1-10")

co("remote/co_a_test40.tsv", type="policies", subtype="policy_apply_dataset", yMax=5, titlePlot="paral, CO_A_1 (10 iterations), 100kb dataset. Replication factor = 1")
co("remote/co_a_test43.tsv", type="policies", subtype="policy_apply_dataset", yMax=5, titlePlot="seq, CO_A_1 (10 iterations), 100kb dataset. Replication factor = 1")

##############
# DO_x
##############

do("remote/do_1_test57.tsv", yMax=0.3, titlePlot="DO_1 (10 iterations), Dataset: 100 files of 100kb text files distributed evenly over domain.", xLabel="Domain size");

do("remote/do_2_test4.tsv", yMax=.15, titlePlot="DO_2 (10 iterations), Dataset: Variable number of 1KB text files.", xLabel="Numer of files", isNumeric=TRUE);

do("remote/do_3_test4.tsv", yMax=.75, titlePlot="DO_3 (10 iterations), Same number of files (60) but different text file datasets.", xLabel="Overall dataset size in domain");


##############
# Failure_x
##############
