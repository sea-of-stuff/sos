setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/os_background.r")
source("r_scripts/exp_basic.r")

library(ggplot2)


d <- read.csv("remote/failure_1_001.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_2_012.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_3_007.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_4_004.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_5_001.tsv", header=TRUE, sep="\t")

# d <- read.csv("remote/failure_6_run_8.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_7_005.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_8_011.tsv", header=TRUE, sep="\t")

# Adding new column to keep track of starting times of iteration
d$StartTime <- 0

ST_index <- grep("^StatsTYPE$", colnames(d))
SUB_index <- grep("^Subtype$", colnames(d))
UM_index <- grep("^User.Measure$", colnames(d))
UM_2_index <- grep("^User.Measure_2$", colnames(d))
UM_3_index <- grep("^User.Measure_3$", colnames(d))
Start_index <- grep("^StartTime$", colnames(d))

index <- -1
startTime <- 0
for(i in 1:nrow(d)) {
  
  if (d[i, ][ST_index] == 'experiment' && d[i, ][SUB_index] == 'experiment') {
    index <- d[i,][UM_2_index] # iteration index
    startTime <- d[i, ][UM_index] # starting time for iteration. Will be taken as reference point for this iteration.
  }
  
  d[i, ][UM_3_index] <- index # add iteration index to other rows
  d[i, ][Start_index] <- startTime # add starting time to other rows
}
  
# Filter the data the has to be displayed
d <- d[d$StatsTYPE != 'experiment',]
d <- d[d$Subtype == 'no_valid_policies',]

d$User.Measure <- (d$User.Measure - d$StartTime) / 1000000000.0; 

ggplot(data=d, aes(x=d$User.Measure, y=d$User.Measure_2, color=d$User.Measure_3, group=d$User.Measure_3)) + 
  scale_colour_gradientn(colours=rainbow(4), guide=FALSE) +
  geom_point(size=.5) +
  geom_line() +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=14),
        plot.title=element_text(size=16),
        legend.title=element_text(size=15),
        legend.text=element_text(size=13)) +
  expand_limits(x = 0, y = 0) +
  labs(title="Percentage of valid assets over time for the context policy satisfied function", x="Time (s)", y="Percentage of valid assets")


