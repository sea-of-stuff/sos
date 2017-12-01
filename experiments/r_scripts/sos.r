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

# Number of iterations: 10
io_1("output/test_io_1_on_1000x1mb_2.tsv", "IO performance. Dataset: 1000 files of 1mb each.")

# Number of iterations: 10
io_1("output/io_1_on_20x50mb_10its.tsv", "IO performance. Dataset: 20 files of 50mb each.")


############
# GUID_1
############

# 10 iterations on the text_100kb dataset
guid_1("output/test_guid_5.tsv", "Performance test on GUID functions. \nDataset: 100 files of 100kb each.")
guid_1("output/test_guid_5.tsv", "Performance test on GUID functions. Dataset: 100 files of 100kb each.", showSummary = TRUE)

# 10 iterations on the random_50mb dataset
guid_1("output/test_guid_4.tsv", "Performance test on GUID functions. \nDataset: 20 files of 50mb each.", showSummary = FALSE)
guid_1("output/test_guid_4.tsv", "Performance test on GUID functions. Dataset: 20 files of 50mb each.", showSummary = TRUE)

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
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset")
po_1("remote/po_a_1_text100kb_10its.tsv", type="policies", subtype="policy_apply_dataset", 
     titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset", showSummary = TRUE)

##############
# PO_A_3
##############

# Number of iterations: 10
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???")
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", showSummary=TRUE)


##############
# PO_C_1
##############

# Number of iterations: 10
po_1("remote/po_c_1_100kb_10its_2.tsv", type="checkPolicies", subtype="policy_check_dataset", titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset")

##############
# PO_C_3
##############

# Number of iterations: 10
po_3("remote/po_c_3_text100kb_its10_1.tsv", type="checkPolicies", titlePlot = "Time to run multiple policy apply functions over ???")
