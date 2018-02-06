setwd("/Users/sic2/git/sos/experiments")
source("r_scripts/os_background.r")
source("r_scripts/exp_basic.r")

library(ggplot2)

d <- read.csv("remote/failure_1_run_9.tsv", header=TRUE, sep="\t")
d <- d[d$StatsTYPE != 'experiment',]
d <- d[d$Subtype == 'no_valid_policies',]

t <- d$User.Measure / 1000000000.0; 
mi <- min(t)
t <- t - mi
plot(d$User.Measure_2~t)


ggplot(data=d, aes(x=t, y=d$User.Measure_2)) + 
  geom_point() +
  geom_line() +
  theme_bw() +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  expand_limits(x = 0, y = 0) +
  labs(title="Number of valid policies over time", x="Time (s)", y="Number of valid policies")
  

# TODO
# - Display vertical line for time when node goes off
# - Display vertical lines for threads regarding policies