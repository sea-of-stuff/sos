setwd("/Users/sic2/git/sos/experiments")

library(ggplot2)
source("r_scripts/utils_stats.r")


# Read the CVS file
d <- read.csv("remote/co_a_test25.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'policies',] # Filter policies measurements
d$Message <- droplevels(d$Message)
d$ContextName <- d$Message

d$ContextName<-factor(d$ContextName, levels=c("do_nothing_policy", 
                                              "data_replication_1", "data_replication_2", "data_replication_3",
                                              "data_replication_4", "data_replication_5", "data_replication_6",
                                              "data_replication_7", "data_replication_8", "data_replication_9",
                                              "data_replication_10"
))

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

dd <- d[d$Subtype == 'policy_apply_dataset',]
dd$Message <- droplevels(dd$Message)
dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
  geom_point() +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  ylim(0, 5) +
  labs(title='TEST', x="Policy", y="Time (s)")
