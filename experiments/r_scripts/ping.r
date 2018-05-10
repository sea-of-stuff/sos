setwd("/Users/sic2/git/sos/experiments")

library(ggplot2)
source("r_scripts/utils_stats.r")

d <- read.csv("remote/ping_1_005.tsv", header=TRUE, sep="\t") # Same Rack
d <- read.csv("remote/ping_1_006.tsv", header=TRUE, sep="\t") # Different Racks

d <- d[d$StatsTYPE == 'ping',]

signed <- FALSE
if (signed) {
  d <- d[d$Message == 'true',]
} else {
  d <- d[d$Message == 'false',]
}
d$Measures <- d$User.Measure / 1000000.0; # Nanoseconds to milliseconds

cat("Average ping time (ms): ", mean(d$Measures))

dd <- summarySE(d, measurevar="Measures")
dd




##################
ratio <- TRUE

d <- read.csv("remote/ping_2_022.tsv", header=TRUE, sep="\t")

d <- d[d$StatsTYPE == 'ping',]
d$User.Measure <- as.numeric(d$User.Measure)
d$User.Measure_2 <- as.numeric(d$User.Measure_2)

d$Size <- (d$User.Measure / 1000000) # size in mb
d$Time <- d$User.Measure_2 / 1000000000.0; # in seconds

yLabel = "N/A"
if (ratio) {
  d$Measures <- d$Size / d$Time; # calculate IO in terms of MB/s
  yLabel = "MB/s"
} else {
  d$Measures <- d$Time; # Nanoseconds to seconds  
  yLabel = "Time (s)"
}

dd <- summarySE(d, measurevar="Measures", groupvars=c("Size", "Message"))
dodge_offset <- 0
ggplot(data=dd, aes(x=dd$Size, y=dd$Measures, color=dd$Message)) + 
  geom_point(position=position_dodge(width=dodge_offset)) +
  geom_errorbar(aes(ymin=dd$Measures-dd$ci, ymax=dd$Measures+dd$ci, color=dd$Message),
                position=position_dodge(width=dodge_offset), width=.2) +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=14),
        legend.title=element_text(size=15),
        legend.text=element_text(size=13)) +
  expand_limits(x = 0, y = 0) +  # Make sure that the min value is 0 on the y-axis
  labs(x="Data size (MB)", y=yLabel) +
  scale_color_discrete(name='Signed\nRequests') +
  guides(col=guide_legend(nrow=2))
  