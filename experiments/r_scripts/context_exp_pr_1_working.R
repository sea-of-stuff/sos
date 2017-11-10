install.packages("PMCMR")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
library(ggplot2)


setwd("/Users/sic2/git/sos/experiments")
getwd()

source("r_scripts/utils_stats.r")
source("r_scripts/kruskal.r")

# random_1  pr_1__2017_11_07T14_24_20_285Z.tsv
# text      pr_1__2017_11_07T14_46_17_213Z
# Read the CVS file
d <- read.csv("remote/test_1kb_500its_3.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'predicate',]
d$Message <- droplevels(d$Message)
d$ContextName <- d$Message

# https://jpwendler.wordpress.com/2013/05/21/reordering-the-factor-levels-in-r-boxplots-and-making-them-look-pretty-with-base-graphics/
d$ContextName<-factor(d$ContextName, levels=c("base", 
                                              "common_word_occurs_once", "uncommon_word_occurs_once", "common_word_occurs_at_least_10_times",
                                              "meta_common_word_occurs_once", "meta_uncommon_word_occurs_once", "meta_common_word_occurs_at_least_10_times",
                                              "metadata", "multi_metadata",
                                              "manifest"
                                              ))

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds


# PLOT TIME TO RUN PREDICATE, PRE-PHASE, POST-PRED-PHASE ETC OVER DATASET
dd <- d[d$Subtype != 'predicate',]
dd$Message <- droplevels(dd$Message)

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE", "Subtype"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures, fill=dd$Subtype)) + 
  geom_bar(stat="identity", position=position_dodge()) +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2,position=position_dodge(.9)) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Time to run different predicates over a single dataset", x="Predicate", y="Time (s)", fill="Run section")


# TIME TO RUN PREDICATE OVER DATASET
dd <- d[d$Subtype == 'predicate_dataset',]
dd$Message <- droplevels(dd$Message)

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))

ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
  geom_point() +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Time to run all predicates on entire dataset", x="Predicate", y="Time (s)")


## STAT ANALYSIS
kruskal(d, d$User.Measure, d$ContextName)

kruskal_dunn(d, d$User.Measure, d$ContextName)
kruskal_nemenyi(d, d$User.Measure, d$ContextName)
