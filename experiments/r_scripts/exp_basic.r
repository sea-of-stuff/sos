library(ggplot2)
library(scales)

source("r_scripts/utils_stats.r")

io_1 <- function(datafile, titlePlot, showSummary=FALSE) {
  d <- read.csv(datafile, header=TRUE, sep="\t") 
  d <- d[d$StatsTYPE == 'io',]
  
  d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  
  # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
  dd <- summarySE(d, measurevar="Measures", groupvars =c("Subtype", "StatsTYPE"))
  
  if (showSummary) {
    dd
  } else {
    ggplot(data=dd, aes(x=dd$Subtype, y=dd$Measures)) + 
      geom_point() +
      geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
      theme_bw() +
      theme(axis.text.x=element_text(angle=90,hjust=1), 
            axis.text=element_text(size=14),
            axis.title=element_text(size=16,face="bold")) +
      scale_y_continuous(labels = comma) + 
      labs(title=titlePlot, x="Operation", y="MB/s")
  }
}

guid_1 <- function(datafile, titlePlot) {
  d <- read.csv(datafile, header=TRUE, sep="\t") 
  d <- d[d$StatsTYPE == 'guid',]
  
  d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  
  # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
  dd <- summarySE(d, measurevar="Measures", groupvars=c("Subtype"))
  dd
}

