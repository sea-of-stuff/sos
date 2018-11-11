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

io("remote/io_2_012.tsv", ratio=TRUE) # 200ms, initH=8, maxH=10   --- 10 its
io("remote/io_2_014.tsv", ratio=TRUE) # 200ms, initH=12, maxH=14  --- 5 its
io("remote/io_2_015.tsv", ratio=TRUE) # 100ms, initH=12, maxH=14  --- 5 its
io("remote/io_2_016.tsv", ratio=TRUE) # 500ms, initH=12, maxH=14  --- 5 its
io("remote/io_2_017.tsv", ratio=TRUE) # 50ms, initH=12, maxH=14  --- 5 its


io("remote/io_2_017.tsv", ratio=TRUE) # 50ms, initH=12, maxH=16  --- 5 its


## IO_2
# TODO - with and without cache invalidation
throughput <- io("remote/io_2_012.tsv", ratio=TRUE)
latency <- io("remote/io_2_012.tsv", ratio=FALSE)

mylegend<-g_legend(throughput)
grid.arrange(arrangeGrob(throughput + theme(legend.position="none"),
                         latency + theme(legend.position="none"),
                         nrow=1,
                         top=textGrob('Input/Output Performance', gp=gpar(fontsize=18))),
             mylegend, nrow=2,heights=c(10, 1))


############
# GUID
############

s <- guid_data("remote/guid_2_004.tsv", showSummary = TRUE, ratio=TRUE)
ci(s, 'md5')
ci(s, 'sha1')
ci(s, 'sha256')
ci(s, 'sha384')
ci(s, 'sha512')


throughput <- guid_data("remote/guid_2_044.tsv", showSummary = FALSE, ratio=TRUE)
latency <- guid_data("remote/guid_2_044.tsv", showSummary = FALSE, ratio=FALSE)

mylegend<-g_legend(throughput)
grid.arrange(arrangeGrob(throughput + theme(legend.position="none"),
                               latency + theme(legend.position="none"),
                               nrow=1,
                         top=textGrob('Hash Algorithms Performance', gp=gpar(fontsize=16))),
                   mylegend, nrow=2,heights=c(10, 1))


##############
# NB_x
##############

nb("output/nb_1_test3.tsv", titlePlot="Normal Behaviour exp.")

##############
# REPL_x
##############

# On the y axis is the time to replicate the entire dataset?

repl("remote/repl_1_004.tsv", subtype="replicate_atom", yMax=11, titlePlot="Atom replication (Dataset: 10MB)");
repl("remote/repl_1_011.tsv", subtype="replicate_atom", yMax=80, titlePlot="Atom replication (Dataset: 100MB)");
repl("remote/repl_1_020.tsv", subtype="replicate_atom", yMax=.8, titlePlot="Atom replication (Dataset: 1MB)");

repl("remote/mr_002.tsv", subtype="replicate_manifest", yMax=1.2, titlePlot="Manifest replication (100 manifests)", numberOfFiles=100);

manifest_repl <- repl("remote/mr_001.tsv", subtype="replicate_manifest", yMax=1.2, titlePlot="Manifest replication (100 manifests)", numberOfFiles=100);
atom_repl_3 <- repl("remote/repl_1_020.tsv", subtype="replicate_atom", yMax=1.2, titlePlot="Atom replication (Dataset: 1MB)");
atom_repl_1 <- repl("remote/repl_1_004.tsv", subtype="replicate_atom", yMax=12, titlePlot="Atom replication (Dataset: 10MB)");
atom_repl_2 <- repl("remote/repl_1_011.tsv", subtype="replicate_atom", yMax=85, titlePlot="Atom replication (Dataset: 100MB)");



mylegend<-g_legend(manifest_repl)
grid.arrange(arrangeGrob(manifest_repl + theme(legend.position="none"),
                         atom_repl_3 + theme(legend.position="none"),
                         atom_repl_1 + theme(legend.position="none"),
                         atom_repl_2 + theme(legend.position="none"),
                         nrow=2,
                         top=textGrob('Manifest and Atom Replication\nTime to replicate contents vs replication factor\n', gp=gpar(fontsize=16))),
             mylegend, nrow=2,heights=c(10, 1))

############
# PR_1
############

# NOTES:
# dataset of all texts
# dataset of mixed content (important to test the value of meta check optimisation on predicate)

pr_1("remote/pr_1_005.tsv", predicateOnly=TRUE, titlePlot="Time to run different predicates over 1GB of text files", yMax=7.5)
pr_1("remote/pr_1_007.tsv", predicateOnly=TRUE, titlePlot="Time to run different predicates over 1GB of data of text and image files", yMax=36)

##############
# PO_A_1
##############

po_1("remote/po_1_002.tsv", titlePlot = "Time to run the policy functions over the R_1MBx1000 dataset")
po_1("remote/po_1_003.tsv", titlePlot = "Time to run the policy functions over the R_1MBx1000 dataset")

po_1("remote/po_1_adobe_10.tsv", titlePlot = "Time to run the policy functions over the R_1MBx1000 dataset")

##############
# PO_A_3 - LEGACY
##############

# Number of iterations: 10
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", yMax=10)
po_3("remote/po_a_3_100kb_its10_3.tsv", type="policies", titlePlot = "Time to run multiple policy apply functions over ???", showSummary=TRUE)


##############
# PO_C_3 - LEGACY
##############

# Number of iterations: 10
po_3("remote/po_c_3_text100kb_its10_1.tsv", type="checkPolicies", titlePlot = "Time to run multiple policy apply functions over ???", yMax=3.5)


##############
# CO_x
##############

co("remote/co_1_006.tsv", titlePlot="CO_1 (10 iterations), 100kb dataset. Replication factor = 1")


co("remote/co_1_016.tsv", titlePlot="Policy over Codomain. RF=1")
co("remote/co_2_010.tsv", titlePlot="Policy over Codomain. RF=[1,10]")


co1 <- co("remote/co_1_016.tsv", titlePlot="Replication Factor is 1") #  1GB dataset.
co2 <- co("remote/co_2_010.tsv", titlePlot="Replication Factor ranges from 1 to 10")

mylegend<-g_legend(co1)
grid.arrange(arrangeGrob(co1 + theme(legend.position="none"),
                         co2 + theme(legend.position="none"),
                         nrow=1,
                         top=textGrob('Time to Enforce and Verify a Replication Policy over a Codomain', gp=gpar(fontsize=16))),
             mylegend, nrow=2, heights=c(10, 1))

##############
# DO_x
##############

do_old("remote/do_1_test57.tsv", yMax=0.3, titlePlot="DO_1 (10 iterations), Dataset: 100 files of 100kb text files\ndistributed evenly over domain.", xLabel="Domain size");
do_old("remote/do_1_006.tsv", yMax=15, titlePlot="DO_1 (10 iterations), Dataset of 1000 text files, domain of 6 nodes max", xLabel="Nodes in domain");

do("remote/do_1_009.tsv", yMax=15, titlePlot="DO_1 (10 iterations), Dataset of 1000 text files (~1GB), domain of 10 nodes max", xLabel="Nodes in domain");
do("remote/do_1_010.tsv", yMax=15, titlePlot="DO_1 (10 iterations), Dataset of 1000 text files (~1GB), domain of 10 nodes max", xLabel="Nodes in domain");
do("remote/do_1_011.tsv", yMax=14, titlePlot="Time to process 1000 text files, of 1MB each, spread evenly across a domain", xLabel="Nodes in domain");

do("remote/do_1_012.tsv", yMax=14, titlePlot="Time to process 1000 text files, of 1MB each, spread evenly across a domain", xLabel="Nodes in domain");

do("remote/do_1_adobe_001.tsv", yMax=14, titlePlot="Time to process 1000 text files, of 1MB each, spread evenly across a domain", xLabel="Nodes in domain");



do_2("remote/do_2_005.tsv", yMax=8, titlePlot="Time to process a variable number of assets, which are spread evenly across a domain", xLabel="Number of assets");

do_old("remote/do_3_test4.tsv", yMax=.75, titlePlot="DO_3 (10 iterations), Same number of files (60) but different text file datasets.", xLabel="Overall dataset size in domain", extractDomainSize=FALSE);

do("remote/do_3_002.tsv", yMax=6.5, titlePlot="Time to process a variable amount of data, stored evenly across a domain of 6 nodes", xLabel="Overall dataset size in domain (MB)", extractDomainSize=FALSE);


##############
# Failure_x
##############
