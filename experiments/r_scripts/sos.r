#######
# SOS EXPERIMENTS
# Plots display means with confidence intervals (.95 confidence level)
########

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/os_background.r")
source("r_scripts/exp_basic.r")
source("r_scripts/exp_pr_1.r")
source("r_scripts/exp_po_1.r")
source("r_scripts/exp_po_3.r")


############
# CPU/MEM
############
mem("output/io_1__2017_11_13T17_09_13_062Z_os.tsv")
cpu("output/io_1__2017_11_13T17_09_13_062Z_os.tsv")
cpu("output/io_1__2017_11_13T17_09_13_062Z_os.tsv", barplot = FALSE)

############
# IO_1
############

# Read the CVS file
# Dataset: text_100kb
# Iterations: 10
io_1("output/io_1__2017_11_13T17_09_13_062Z.tsv", "plot title") # With cache invalidation
io_1("output/io_1__2017_11_13T17_19_29_095Z.tsv", "plot title") # Without cache invalidation

io_1("output/test_io_1_on_1000x1mb_2.tsv", "plot title")
io_1("output/test_io_1_on_20x50mb_1.tsv", "plot title")


############
# GUID_1
############

guid_1("output/test_guid_1.tsv", "Performance test on the GUID function. GUID - SHA-2(-256)")

############
# PR_1
############

pr_1("remote/test_1kb_500its_3.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 1kb dataset")
pr_1("remote/test_1kb_500its_3.tsv", predicateOnly=FALSE, titlePlot="Time to run a predicate and the pre-post predicate functions over the 1kb dataset")

# Removing measurements that were not performed correctly.
pr_1("remote/test_1kb_500its_3_cleaned.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the ~9000 x 1kB dataset")
pr_1("remote/test_1kb_500its_3_cleaned.tsv", predicateOnly=FALSE, titlePlot="Time to run a predicate and the pre-post predicate functions over the 1kb dataset")

pr_1("remote/text_100kb_100its.tsv", predicateOnly=TRUE, titlePlot="Time to run a predicate over the 100x100kb dataset")

##############
# PO_A_1
##############

po_1("remote/testmon3.tsv", type="policies", subtype="policy_apply_dataset", titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset")


##############
# PO_A_3
##############

po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???")
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", showSummary=TRUE)


##############
# PO_C_1
##############

po_1("remote/po_c_1_100kb_10its_2.tsv", type="checkPolicies", subtype="policy_check_dataset", titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset")

##############
# PO_C_3
##############

po_3("remote/po_c_3_text100kb_its10_1.tsv", type="checkPolicies", titlePlot = "Time to run multiple policy apply functions over ???")
