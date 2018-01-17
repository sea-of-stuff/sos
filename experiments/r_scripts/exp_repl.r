setwd("/Users/sic2/git/sos/experiments")
library(ggplot2)

source("r_scripts/utils_stats.r")

# Message_2 --> true == sequential, false == parallel
# User.Measue_2 --> replicationFactor
d <- read.csv("remote/repl_1_test4.tsv", header=TRUE, sep="\t")
d <- d[d$Subtype == 'replicate_atom',]
d$ContextName <- sprintf("Sequential: %s, R.F.: %d", d$Message_2, d$User.Measure_2)

numberOfFiles = 100
d$Measures <- (d$User.Measure / 1000000000.0) * numberOfFiles; # Nanoseconds to seconds
dd <- summarySE(d, measurevar="Measures", groupvars =c("ContextName"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
  geom_point() +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  ylim(0, 4.25) +
  labs(title="Replication per atom (atom size: 100KB)", x="Replication Type and Replication Factor", y="Time (s)")
