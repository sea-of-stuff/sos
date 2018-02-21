setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/os_background.r")
source("r_scripts/exp_basic.r")

library(ggplot2)

d <- read.csv("remote/failure_1_run_24.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/failure_2_run_5.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/failure_3_run_3.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/failure_4_run_1.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_5_run_2.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/failure_5_run_3.tsv", header=TRUE, sep="\t")

d <- read.csv("remote/failure_6_run_8.tsv", header=TRUE, sep="\t")
d <- read.csv("remote/failure_7_run_1.tsv", header=TRUE, sep="\t")

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
  scale_colour_continuous(guide = FALSE) +
  # scale_colour_gradientn(colours=rainbow(4), guide=FALSE) +
  geom_point(size=.5) +
  geom_line() +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  expand_limits(x = 0, y = 0) +
  labs(title="Number of replicas in codomain over time", x="Time (s)", y="Number of replicas")


########################
#### OLD CODE ##########
########################

toggleAPI <- d[d$StatsTYPE == 'experiment' & d$Subtype == 'ping',]$User.Measure / 1000000000.0
vlines <- data.frame(xint = c(toggleAPI))

d <- d[d$StatsTYPE != 'experiment',]
d <- d[d$Subtype == 'no_valid_policies',]

t <- d$User.Measure / 1000000000.0; 
mi <- min(t)
t <- t - mi
vlines$xint <- vlines$xint - mi 

ggplot(data=d, aes(x=t, y=d$User.Measure_2)) + 
  geom_point() +
  geom_line() +
  geom_vline(data=vlines, aes(xintercept=xint, colour="Red"), linetype="longdash") +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  expand_limits(x = 0, y = 0) +
  labs(title="Number of valid policies over time", x="Time (s)", y="Number of valid policies")
  


# TODO
# - Display vertical lines for threads regarding policies