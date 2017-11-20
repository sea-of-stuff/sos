#######
# SOS EXPERIMENTS
########

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/io.r")
source("r_scripts/exp_pr_1.r")
source("r_scripts/exp_po_1.r")
source("r_scripts/exp_po_3.r")

############
# IO_1
############

# Read the CVS file
# Dataset: text_100kb
# Iterations: 10
io_1("output/io_1__2017_11_13T17_09_13_062Z.tsv", "test") # With cache invalidation
io_1("output/io_1__2017_11_13T17_19_29_095Z.tsv", "test") # Without cache invalidation

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
# PO_1
##############

po_1("remote/testmon3.tsv", titlePlot = "Time to run the policy apply function over the ~100 x 100kB dataset")


##############
# PO_3
##############

po_3("output/po_3__2017_11_07T13_47_00_600Z.tsv", titlePlot = "Time to run multiple policy apply functions over ???")

