#######
# SOS EXPERIMENTS
# Plots display means with confidence intervals (.95 confidence level)
########

library(gridExtra)
library(grid)

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/utils_stats.r")
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
io_1("output/io_1__2017_11_13T17_09_13_062Z.tsv", "output/io_1__2017_11_13T17_19_29_095Z.tsv")

io_1("remote/io_1_003.tsv", "remote/io_1_002.tsv")
io_1("remote/io_1_003.tsv", "remote/io_1_002.tsv", manifestsOnly=TRUE)


io("output/local_io_2_011.tsv", ratio=TRUE)
io("output/local_io_2_011.tsv", ratio=FALSE)

io("remote/io_2_012.tsv", ratio=TRUE)
io("remote/io_2_012.tsv", ratio=FALSE)


## IO_2
# TODO - with and without cache invalidation
throughput <- io("remote/io_2_010.tsv", ratio=TRUE)
latency <- io("remote/io_2_010.tsv", ratio=FALSE)

mylegend<-g_legend(throughput)
grid.arrange(arrangeGrob(throughput + theme(legend.position="none"),
                         latency + theme(legend.position="none"),
                         nrow=1,
                         top=textGrob('IO Performance', gp=gpar(fontsize=18))),
             mylegend, nrow=2,heights=c(10, 1))

mem("remote/io_2_004_os.tsv")
cpu("remote/io_2_004_os.tsv")


############
# GUID
############

s <- guid_data("remote/guid_2_004.tsv", showSummary = TRUE, ratio=TRUE)
ci(s, 'md5')
ci(s, 'sha1')
ci(s, 'sha256')
ci(s, 'sha384')
ci(s, 'sha512')


throughput <- guid_data("remote/guid_2_004.tsv", showSummary = FALSE, ratio=TRUE)
latency <- guid_data("remote/guid_2_004.tsv", showSummary = FALSE, ratio=FALSE)

mylegend<-g_legend(throughput)
grid.arrange(arrangeGrob(throughput + theme(legend.position="none"),
                               latency + theme(legend.position="none"),
                               nrow=1,
                         top=textGrob('Hash Algorithms Performance', gp=gpar(fontsize=16))),
                   mylegend, nrow=2,heights=c(10, 1))

mem("remote/guid_2_run_3.tsv")
cpu("remote/guid_2_run_3.tsv")

##############
# NB_x
##############

nb("output/nb_1_test3.tsv", titlePlot="Normal Behaviour exp.")

##############
# REPL_x
##############

repl("remote/dr_005.tsv", subtype="replicate_atom", yMax=8, titlePlot="Data replication (Dataset: 100KBx1000files). 10 Iterations");
repl("remote/mr_001.tsv", subtype="replicate_manifest", yMax=1.2, titlePlot="Manifests Repl...");

atom_repl <- repl("remote/dr_005.tsv", subtype="replicate_atom", yMax=8, titlePlot="Data replication (Dataset: 100KBx1000files). 10 Iterations");
manifest_repl <- repl("remote/mr_001.tsv", subtype="replicate_manifest", yMax=1.2, titlePlot="Manifest replication (100 Version manifests). 10 Iterations");

mylegend<-g_legend(atom_repl)
grid.arrange(arrangeGrob(atom_repl + theme(legend.position="none"),
                         manifest_repl + theme(legend.position="none"),
                         nrow=1,
                         top=textGrob('Content Replication', gp=gpar(fontsize=16))),
             mylegend, nrow=2,heights=c(10, 1))


############
# PR_1
############

# NOTES:
# dataset of all texts
# dataset of mixed content (important to test the value of meta check optimisation on predicate)

pr_1("remote/pr_1_text100kb_20its.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 1000x100kb dataset (20 its)")


# NEW EXPS

pr_1("remote/pr_1_run_4.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 1000x100kb dataset (20 its)")

pr_1("remote/pr_1_run_5.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset (20 its)")

pr_1("output/test_pr_1_11.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset (20 its)")
pr_1("output/test_pr_1_12.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset (20 its)")
pr_1("output/test_pr_1_13.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset (20 its)")
pr_1("output/test_pr_1_14.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset (20 its)")
pr_1("output/test_pr_1_16.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the Random_1 dataset (20 its)")

pr_1("remote/pr_1_005.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the text100kb_3 dataset (10 iterations)")

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

do("remote/do_1_test57.tsv", yMax=0.3, titlePlot="DO_1 (10 iterations), Dataset: 100 files of 100kb text files\ndistributed evenly over domain.", xLabel="Domain size");

do("remote/do_2_test4.tsv", yMax=.15, titlePlot="DO_2 (10 iterations), Dataset: Variable number of 1KB text files.", xLabel="Numer of files", isNumeric=TRUE, extractDomainSize=FALSE);

do("remote/do_3_test4.tsv", yMax=.75, titlePlot="DO_3 (10 iterations), Same number of files (60) but different text file datasets.", xLabel="Overall dataset size in domain", extractDomainSize=FALSE);


##############
# Failure_x
##############
