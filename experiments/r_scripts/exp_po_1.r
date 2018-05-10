po_1 <- function(datafile, titlePlot="NO TITLE") {
  library(ggplot2)
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t")
  d$StatsTYPE <- as.character(d$StatsTYPE)
  d$StatsTYPE[d$StatsTYPE == "policies"] <- "Apply"
  d$StatsTYPE[d$StatsTYPE == "checkPolicies"] <- "Check"
  
  d$Message <- droplevels(d$Message)
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds
  
  dd <- summarySE(d, measurevar="Measures", groupvars=c("Message", "StatsTYPE"))
  
  ggplot(data=dd, aes(x=dd$Message, y=dd$Measures, color=dd$StatsTYPE)) + 
    geom_point() +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci, color=dd$StatsTYPE),width=.2) +
    theme_bw() +
    theme(axis.text.x=element_text(angle=90,hjust=1), 
          axis.text=element_text(size=14),
          axis.title=element_text(size=16,face="bold")) +
    labs(title=titlePlot, x="Policy", y="Time (s)") +
    scale_color_discrete(name='Policy Function')

}

