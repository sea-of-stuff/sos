library(ggplot2)
library(scales)

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/utils_stats.r")

datafile <- "remote/guid_2_run_3.tsv"
d <- read.csv(datafile, header=TRUE, sep="\t") 
d <- d[d$StatsTYPE == 'guid_data',]

yLabel = "N/A"
if (ratio) {
  d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  yLabel = "MB/s"
} else {
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds  
  yLabel = "Time (s)"
}


d$Size <- (d$Message / 1000000) # size in mb

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(d, measurevar="Measures", groupvars=c("Subtype", "Size"))

if (showSummary) {
  dd
} else {
  ggplot(data=dd, aes(x=dd$Size, y=dd$Measures, color=dd$Subtype)) + 
    geom_point() +
    geom_line() +
    geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
    theme_bw() +
    theme(axis.text.x=element_text(angle=90,hjust=1), 
          axis.text=element_text(size=14),
          axis.title=element_text(size=16,face="bold")) +
    scale_y_continuous(labels = comma) + 
    expand_limits(x = 0, y = 0) +
    labs(title="SHA Algorithms performance", x="Data size (MB)", y=yLabel) +
    scale_color_discrete(name='SHA Algorithm')
}
