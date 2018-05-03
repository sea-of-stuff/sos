setwd("/Users/sic2/git/sos/experiments")

library(ggplot2)
source("r_scripts/utils_stats.r")

# same results with ping_test3.tsv
# ping_1_run_april_1.tsv
d <- read.csv("remote/ping_1_001.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/ping_2_012.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'ping',]
# d$Message <- droplevels(d$Message)
# d$ContextName <- d$Message

d$Measures <- d$User.Measure / 1000000.0; # Nanoseconds to milliseconds

cat("Average ping time (ms): ", mean(d$Measures))

dd <- summarySE(d, measurevar="Measures")
dd
