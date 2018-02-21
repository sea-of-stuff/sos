repl <- function(datafile, subtype, titlePlot="NO TITLE", showSummary=FALSE, yMax, isNumeric=FALSE) {
  
  library(ggplot2)
  source("r_scripts/utils_stats.r")
  
  # Message_2 --> true == sequential, false == parallel
  # User.Measue_2 --> replicationFactor
  d <- read.csv(datafile, header=TRUE, sep="\t")
  d <- d[d$Subtype == subtype,]
  d$ContextName <- sprintf("R.F.: %d", d$User.Measure_2)
  
  numberOfFiles = 100
  d$Measures <- (d$User.Measure / 1000000000.0) * numberOfFiles; # Nanoseconds to seconds
  dd <- summarySE(d, measurevar="Measures", groupvars=c("Message_2", "ContextName"))
  
  if (showSummary) {
    dd
  } else {
    ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures, color=dd$Message_2)) + 
      geom_point() +
      geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
      theme_bw() +
      theme(axis.text.x=element_text(angle=90,hjust=1), 
            axis.text=element_text(size=14),
            axis.title=element_text(size=16,face="bold")) +
      ylim(0, yMax) +
      labs(title=titlePlot, x="Replication Factor", y="Time (s)") +
      scale_color_discrete(name='Sequential\nReplication')
  }
}