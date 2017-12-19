setwd("/Users/sic2/git/sos/experiments")

library(ggplot2)
source("r_scripts/utils_stats.r")

# same results with ping_test3.tsv
d <- read.csv("remote/ping_test4.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'ping',]
# d$Message <- droplevels(d$Message)
# d$ContextName <- d$Message

d$Measures <- d$User.Measure / 1000000.0; # Nanoseconds to milliseconds

cat("Average ping time (ms): ", mean(d$Measures))
