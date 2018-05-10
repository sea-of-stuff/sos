po_1 <- function(datafile, titlePlot="NO TITLE") {
  library(ggplot2)
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t")
  d$StatsTYPE <- as.character(d$StatsTYPE)
  d$StatsTYPE[d$StatsTYPE == "policies"] <- "Apply"
  d$StatsTYPE[d$StatsTYPE == "checkPolicies"] <- "Check"
  
  d$Message <- as.character(d$Message)
  d$Message[d$Message == "data_replication_1"] <- "Atom Replication"
  d$Message[d$Message == "do_nothing_policy"] <- "Void Policy"
  d$Message[d$Message == "manifest_replication_1"] <- "Manifest Replication"
  
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds
  
  dd <- summarySE(d, measurevar="Measures", groupvars=c("Message", "StatsTYPE"))
  
  ggplot(data=dd, aes(x=dd$Message, y=dd$Measures, color=dd$StatsTYPE)) + 
    geom_point() +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci, color=dd$StatsTYPE),width=.2) +
    theme_bw() +
    theme(axis.text=element_text(size=14),
          axis.title=element_text(size=14),
          plot.title=element_text(size=16),
          legend.title=element_text(size=15),
          legend.text=element_text(size=13)) +
    labs(title=titlePlot, x="Policies", y="Time (s)") +
    scale_color_discrete(name='Function')

}

