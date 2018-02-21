do <- function(datafile, titlePlot="NO TITLE", xLabel="No Label", showSummary=FALSE, yMax, isNumeric=FALSE, extractDomainSize=TRUE) {
  
  library(ggplot2)
  source("r_scripts/utils_stats.r")

  d <- read.csv(datafile, header=TRUE, sep="\t")
  d <- d[d$StatsTYPE == 'predicate_remote',]
  if (!isTRUE(isNumeric)) {
    d$Message <- droplevels(d$Message)
  }
  if (extractDomainSize) {
    d$ContextName <- sapply(strsplit(as.character(d$Message), '_'), '[', 2)
  } else {
    d$ContextName <- d$Message
  }
  
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds
  
  dd <- d[d$Subtype == 'predicate_dataset',]
  if (!isTRUE(isNumeric)) {
    dd$Message <- droplevels(dd$Message)
  }
  dd <- summarySE(dd, measurevar="Measures", groupvars =c("ContextName", "StatsTYPE"))
  
  if (showSummary) {
    dd
  } else {
    ggplot(data=dd, aes(x=dd$ContextName, y=dd$Measures)) + 
      geom_point() +
      geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
      theme_bw() +
      theme(axis.text.x=element_text(angle=90,hjust=1), 
            axis.text=element_text(size=14),
            axis.title=element_text(size=16,face="bold")) +
      ylim(0, yMax) +
      labs(title=titlePlot, x=xLabel, y="Time (s)")
  }
  
}