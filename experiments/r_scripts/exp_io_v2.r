library(ggplot2)
library(scales)

setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/utils_stats.r")

dataFile <- "output/io_2_run_3.tsv"
d <- read.csv(dataFile, header=TRUE, sep="\t", stringsAsFactors=F) 

d$StatsTYPE <- as.character(d$StatsTYPE)
d$Subtype[d$StatsTYPE == "guid_data"] <- "guid_data"
d$StatsTYPE[d$Subtype == "guid_data"] <- "io"
d <- d[d$StatsTYPE == 'io',]

# Exclude
d <- d[d$Subtype != 'replicate_atom',]
d <- d[d$Subtype != 'replicate_manifest',]

# https://jpwendler.wordpress.com/2013/05/21/reordering-the-factor-levels-in-r-boxplots-and-making-them-look-pretty-with-base-graphics/
d$Subtype<-factor(d$Subtype, levels=c("fs_write_file", "fs_read_file",
                                      "add_atom", "guid_data", "add_manifest",
                                      "read_atom", "read_manifest", "replicate_atom", "replicate_manifest"
))

d$Message <- as.numeric(d$Message)
d$User.Measure <- as.numeric(d$User.Measure)

d$Size <- (d$Message / 1000000) # size in mb

yLabel = "N/A"
if (ratio) {
  d$Measures <- (d$Message / 1000000) / (d$User.Measure / 1000000000.0); # calculate IO in terms of MB/s
  yLabel = "MB/s"
} else {
  d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds  
  yLabel = "s"
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
          axis.text=element_text(size=14),
          axis.title=element_text(size=16,face="bold")) +
    scale_y_continuous(labels = comma) + 
    expand_limits(x = 0, y = 0) +  # Make sure that the min value is 0 on the y-axis
    labs(title="Add Atom", x="Data size", y=yLabel) +
    scale_color_discrete(name='Operation Types')
}