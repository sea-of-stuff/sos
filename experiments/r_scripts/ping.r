setwd("/Users/sic2/git/sos/experiments")

library(ggplot2)
source("r_scripts/utils_stats.r")

# same results with ping_test3.tsv
# ping_1_run_april_1.tsv
d <- read.csv("remote/ping_1_001.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/ping_2_012.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'ping',]
# d$Message <- droplevels(d$Message)
# d$ContextName <- d$Message

d$Measures <- d$User.Measure / 1000000.0; # Nanoseconds to milliseconds

cat("Average ping time (ms): ", mean(d$Measures))

dd <- summarySE(d, measurevar="Measures")
dd




##################
ratio <- FALSE

d <- read.csv("remote/ping_2_17.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE == 'ping',]
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

dd <- summarySE(d, measurevar="Measures", groupvars =c("Size"))

ggplot(data=dd, aes(x=dd$Size, y=dd$Measures)) + 
  geom_point() +
  geom_line() +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci),width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=14),
        legend.title=element_text(size=15),
        legend.text=element_text(size=13)) +
  scale_y_continuous(labels = comma) + 
  expand_limits(x = 0, y = 0) +  # Make sure that the min value is 0 on the y-axis
  labs(x="Data size (MB)", y=yLabel) +
  scale_color_discrete(name='Operation Types') +
  guides(col=guide_legend(nrow=2))
