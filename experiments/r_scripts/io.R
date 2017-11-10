library(ggplot2)
source("r_scripts/utils_stats.r")

setwd("/Users/sic2/git/sos/experiments")
getwd()

# Read the CVS file
d <- read.csv("output/io_1__2017_11_10T11_38_36_202Z.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'io',] # Filter policies measurements

d$Measures <- d$User.Measure / 1000000000.0; # Nanoseconds to seconds

# http://www.cookbook-r.com/Graphs/Plotting_means_and_error_bars_(ggplot2)/
dd <- summarySE(d, measurevar="Measures", groupvars =c("Subtype", "StatsTYPE"))

ggplot(data=dd, aes(x=dd$Subtype, y=dd$Measures)) + 
  geom_point() +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Write IO on data and manifests", x="Type of entity", y="Time (s)")

