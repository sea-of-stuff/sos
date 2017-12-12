
io_1 <- function(datafile, titlePlot, showSummary=FALSE, ratio=TRUE) {
  library(ggplot2)
  library(scales)
  
  source("r_scripts/utils_stats.r")
  
  setwd("/Users/sic2/git/sos/experiments")
  dataFile <- "output/io_1_on_text100k_10its_1.tsv"
  d <- read.csv(datafile, header=TRUE, sep="\t", stringsAsFactors=F) 
  
  d$StatsTYPE <- as.character(d$StatsTYPE)
  d$Subtype[d$StatsTYPE == "guid_data"] <- "guid_data"
  d$StatsTYPE[d$Subtype == "guid_data"] <- "io"
  d <- d[d$StatsTYPE == 'io',]
  
  # https://jpwendler.wordpress.com/2013/05/21/reordering-the-factor-levels-in-r-boxplots-and-making-them-look-pretty-with-base-graphics/
  d$Subtype<-factor(d$Subtype, levels=c("fs_write_file", "fs_read_file",
                                        "add_atom", "guid_data", "add_manifest",
                                        "read_atom", "read_manifest"
  ))
  
  yLabel = "N/A"
  if (ratio) {
    d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
    yLabel = "MB/s"
  } else {
    d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds  
    yLabel = "s"
  }
  
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
      expand_limits(x = 0, y = 0) +  # Make sure that the min value is 0 on the y-axis
      labs(title=titlePlot, x="Operation", y=yLabel)
  }
}

guid <- function(datafile, statsType, titlePlot, showSummary=FALSE) {
  library(ggplot2)
  library(scales)
  
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t") 
  d <- d[d$StatsTYPE == statsType,]
  
  d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  
  # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
  dd <- summarySE(d, measurevar="Measures", groupvars=c("Subtype"))
  
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
      expand_limits(x = 0, y = 0) +
      labs(title=titlePlot, x="Operation", y="MB/s")
  }
}

guid_data <- function(datafile, titlePlot, showSummary=FALSE) {
  
  guid(datafile, 'guid_data', titlePlot, showSummary)
}

guid_manifest <- function(datafile, titlePlot, showSummary=FALSE) {
  
  guid(datafile, 'guid_manifest', titlePlot, showSummary)
}
