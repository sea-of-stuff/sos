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
d <- read.csv("output/po_1__2017_10_31T12_43_49_502Z.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'policies',] # Filter policies measurements
d$Message <- droplevels(d$Message)
d$ContextName <- d$Message

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

################
# Playing with ggplot

ggplot(data=d, aes(x=d$ContextName, y=d$Measures)) + 
  geom_boxplot(outlier.alpha = 0.1) +
  geom_point(color="tomato", position="jitter", alpha=.05) +
  geom_rug(sides="l") +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Policies per asset....", x="Policy", y="Time (s)")


ggplot(data=d, aes(x=d$ContextName, y=d$Measures)) + 
  geom_bar(position=position_dodge(), stat="identity", width=.5) +
  geom_point(color="tomato", position="jitter", alpha=.05) +
  geom_rug(sides="l") +
  labs(title="Policies per asset....", x="Policy", y="Time (s)")
