co <- function(datafile, titlePlot="NO TITLE") {

  library(ggplot2)
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t")
  d$StatsTYPE <- as.character(d$StatsTYPE)
  d$StatsTYPE[d$StatsTYPE == "policies"] <- "Apply"
  d$StatsTYPE[d$StatsTYPE == "checkPolicies"] <- "Check"
  
  d$Message <- as.character(d$Message)
  d <- d[d$Message != 'do_nothing_policy',]
  d$Message[d$Message == "data_replication_10"] <- "10"
  d$Message[d$Message == "data_replication_1"] <- "1"
  d$Message[d$Message == "data_replication_2"] <- "2"
  d$Message[d$Message == "data_replication_3"] <- "3"
  d$Message[d$Message == "data_replication_4"] <- "4"
  d$Message[d$Message == "data_replication_5"] <- "5"
  d$Message[d$Message == "data_replication_6"] <- "6"
  d$Message[d$Message == "data_replication_7"] <- "7"
  d$Message[d$Message == "data_replication_8"] <- "8"
  d$Message[d$Message == "data_replication_9"] <- "9"
  d$ContextName <- as.numeric(d$Message)
  #d$ContextName<-factor(d$ContextName, levels=c("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
  
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds
  
  dd <- summarySE(d, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))

  ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures, color=dd$StatsTYPE)) + 
    geom_point() +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci, color=dd$StatsTYPE),width=.2) +
    theme_bw() +
    theme(axis.text=element_text(size=14),
          axis.title=element_text(size=14),
          plot.title=element_text(size=16),
          legend.title=element_text(size=15),
          legend.text=element_text(size=13)) +
    labs(title=titlePlot, x="Nodes in Domain", y="Time (s)") +
    scale_color_discrete(name='Function') +
    scale_x_continuous(breaks=seq(0,10,1)) +
    guides(col=guide_legend(nrow=1))
}