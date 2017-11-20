library(ggplot2)

setwd("/Users/sic2/git/sos/experiments")
getwd()

source("r_scripts/utils_stats.r")

po_3 <- function(datafile, titlePlot="NO TITLE") {
  d <- read.csv(datafile, header=TRUE, sep="\t")
  d <- d[d$StatsTYPE == 'policies',] # Filter policies measurements
  d$Message <- droplevels(d$Message)
  d$ContextName <- d$Message
  
  d$ContextName<-factor(d$ContextName, levels=c("no_policies", 
                                                "one_policy_remote", "two_policies_remote", "three_policies_remote"
  ))
  
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds
  
  dd <- summarySE(d, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))
  
  ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
    geom_point() +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
    theme_bw() +
    theme(axis.text.x=element_text(angle=90,hjust=1), 
          axis.text=element_text(size=14),
          axis.title=element_text(size=16,face="bold")) +
    labs(title=titlePlot, x="Policy", y="Time (s)")
}
