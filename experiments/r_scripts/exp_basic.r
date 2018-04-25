io_1 <- function(datafile, datafile_with_cache) {
  library(ggplot2)
  library(scales)
  
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t") 
  d <- d[d$StatsTYPE == 'io',]
  d$cache <- 'No'
  
  d_wc <- read.csv(datafile_with_cache, header=TRUE, sep="\t") 
  d_wc <- d_wc[d_wc$StatsTYPE == 'io',]
  d_wc$cache <- 'Yes'
  
  # Join results
  d_new <- rbind(d, d_wc)
  
  d_new$Subtype<-factor(d_new$Subtype, levels=c("add_atom", "read_atom", "add_manifest", "read_manifest"))
  
  d_new$Measures <- (d_new$Message / 1000000) / (d_new$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  
  # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
  dd <- summarySE(d_new, measurevar="Measures", groupvars =c("Subtype", "StatsTYPE", "cache"))
  
  dodge_offset <- 0.6
  ggplot(data=dd, aes(x=dd$Subtype, y=dd$Measures, group=dd$cache, color=dd$cache)) + 
    geom_point(position=position_dodge(width=dodge_offset)) +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci, color=dd$cache),
                  position=position_dodge(width=dodge_offset), width=.2) +
    theme_bw() +
    theme(axis.text.x=element_text(angle=90,hjust=1), 
          axis.text=element_text(size=14),
          axis.title=element_text(size=14),
          legend.title=element_text(size=13),
          legend.text=element_text(size=11),
          legend.justification=c(0,0), legend.position=c(0.05,.85),
          legend.background = element_rect(fill=alpha('white', 0))) +
    scale_y_continuous(labels = comma) + 
    labs(title="IO Performance", x="IO Operations", y="MB/s") +
    scale_color_discrete(name='Using cache')
}



io <- function(datafile, showSummary=FALSE, ratio=TRUE) {
  library(ggplot2)
  library(gridExtra)
  library(scales)
  
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t", stringsAsFactors=F) 
  
  d$StatsTYPE <- as.character(d$StatsTYPE)
  d$Subtype[d$StatsTYPE == "guid_data"] <- "guid_data"
  d$StatsTYPE[d$Subtype == "guid_data"] <- "io"
  d <- d[d$StatsTYPE == 'io',]
  
  # Exclude
  d <- d[d$Subtype != 'replicate_atom',]
  d <- d[d$Subtype != 'replicate_manifest',]
  d <- d[d$Subtype != 'add_manifest',]
  d <- d[d$Subtype != 'read_manifest',]
  
  # https://jpwendler.wordpress.com/2013/05/21/reordering-the-factor-levels-in-r-boxplots-and-making-them-look-pretty-with-base-graphics/
  d$Subtype<-factor(d$Subtype, levels=c("fs_write_file", "fs_read_file",
                                        "add_atom", "guid_data", "add_manifest",
                                        "read_atom", "read_manifest", "replicate_atom", "replicate_manifest"
  ))
  
  d$Message <- as.numeric(d$Message)
  d$User.Measure <- as.numeric(d$User.Measure)
  
  d$Size <- (d$Message / 1000000) # size in mb
  d$Time <- d$User.Measure / 1000000000.0; # in seconds
  
  yLabel = "N/A"
  if (ratio) {
    d$Measures <- d$Size / d$Time; # calculate IO in terms of MB/s
    yLabel = "MB/s"
  } else {
    d$Measures <- d$Time; # Nanoseconds to seconds  
    yLabel = "Time (s)"
  }
  
  # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
  dd <- summarySE(d, measurevar="Measures", groupvars =c("Subtype", "Size"))
  
  if (showSummary) {
    dd
  } else {
    ggplot(data=dd, aes(x=dd$Size, y=dd$Measures, color=dd$Subtype)) + 
      geom_point() +
      geom_line() +
      geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
      theme_bw() +
      theme(axis.text.x=element_text(angle=90,hjust=1), 
            axis.text=element_text(size=12),
            axis.title=element_text(size=12),
            legend.title=element_text(size=13),
            legend.text=element_text(size=11)) +
      scale_y_continuous(labels = comma) + 
      expand_limits(x = 0, y = 0) +  # Make sure that the min value is 0 on the y-axis
      labs(x="Data size (MB)", y=yLabel) +
      scale_color_discrete(name='Operation Types') +
      guides(col=guide_legend(nrow=2))
  }
}

guid <- function(datafile, statsType, showSummary=FALSE, ratio) {
  library(ggplot2)
  library(gridExtra)
  library(grid)
  library(scales)
  
  source("r_scripts/utils_stats.r")
  
  d <- read.csv(datafile, header=TRUE, sep="\t") 
  d <- d[d$StatsTYPE == statsType,]

  d$Size <- (d$Message / 1000000) # size in mb
  
  if(ratio) {
    d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
    yLabel = "MB/s"
  } else {
    d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds  
    yLabel = "Time (s)"
  }
  
  if (showSummary) {
    # http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
    dd <- summarySE(d, measurevar="Measures", groupvars=c("Subtype", "Size"))
    dd
  } else {
  
    dd <- summarySE(d, measurevar="Measures", groupvars=c("Subtype", "Size"))
    ggplot(data=dd, aes(x=dd$Size, y=dd$Measures, color=dd$Subtype)) + 
      geom_point() +
      geom_line() +
      geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
      theme_bw() +
      theme(axis.text.x=element_text(angle=90,hjust=1), 
            axis.text=element_text(size=12),
            axis.title=element_text(size=12),
            legend.title=element_text(size=13),
            legend.text=element_text(size=11)) +
      expand_limits(x = 0, y = 0) +
      labs(x="Data size (MB)", y=yLabel) +
      scale_color_discrete(name='Hash Algorithms') +
      guides(col=guide_legend(nrow=1))
  }
}

guid_data <- function(datafile, showSummary=FALSE, ratio) {
  
  guid(datafile, 'guid_data', showSummary, ratio)
}
