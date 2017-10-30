install.packages("PMCMR")
install.packages("FSA")
library("PMCMR", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")
library("FSA", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

library("fitdistrplus", lib.loc="/Library/Frameworks/R.framework/Versions/3.3/Resources/library")

install.packages("stargazer")
library(stargazer)

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("output/po_1__2017_10_27T14_40_19_356Z.tsv", header=TRUE, sep="\t")
d$ContextName <- d$Message # sapply(strsplit(as.character(d$Message), '_'), '[', 1) # Split by 'SHA' if we want to look at the individual contexts

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

aggr <- aggregate(d$Measures ~ d$ContextName,
                  FUN = function(x) c(mean = mean(x), sd = sd(x), n = length(x)))

d_processed <- do.call(data.frame, aggr)

# Compute standard error per group
d_processed$se <- d_processed[,3] / sqrt(d_processed[,4])

# Rename columns
colnames(d_processed) <- c("Configuration", "mean", "sd", "n", "se")
d_processed$names <- d_processed$Config

##################
# BOXPLOT
par(mar=c(20,4,4,2)+3) # Add space to show all labels
x <- boxplot(d$Measures~d$ContextName, data=d,
             outline=FALSE,
             las=2, # Draw x labels vertically
             ylab="Time (s) - log scale", 
             log = "y")

