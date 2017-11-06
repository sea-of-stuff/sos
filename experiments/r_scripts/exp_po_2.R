install.packages("PMCMR")
install.packages("FSA")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
library("FSA", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

library("fitdistrplus", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

install.packages("stargazer")
library(stargazer)

library(ggplot2)

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("output/po_2__2017_11_06T14_58_41_690Z.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'policies',] # Filter policies measurements
d$Message <- droplevels(d$Message)
d$ContextName <- d$Message

d$ContextName<-factor(d$ContextName, levels=c("no_policies", 
                                              "one_policy_local", "two_policies_local", "three_policies_local",
                                              "four_policies_local", "five_policies_local", "six_policies_local",
                                              "seven_policies_local", "eight_policies_local", "nine_policies_local", "ten_policies_local"
))


d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

aggr <- aggregate(d$Measures ~ d$ContextName,
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed[,3] / sqrt(d_processed[,4])

# Rename columns
colnames(d_processed) <- c("Configuration", "mean", "sd", "n", "se")
d_processed$names <- d_processed$Config

colors = c(rep("red",1),rep("deepskyblue",2),rep("green",1))


################
# Playing with ggplot

ggplot(data=d, aes(x=d$ContextName, y=d$Measures)) + 
  geom_boxplot(outlier.alpha = 1) +
  geom_point(color="tomato", position="jitter", alpha=.5) +
  geom_rug(side="1") +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Policies per asset....", x="Policy", y="Time (s)")


ggplot(data=d, aes(x=d$ContextName, y=d$Measures)) + 
  geom_bar(position=position_dodge(), stat="identity", width=.5) +
  geom_point(color="tomato", position="jitter", alpha=.5) +
  geom_rug(side="1") +
  labs(title="Policies per asset....", x="Policy", y="Time (s)")
