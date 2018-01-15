#
# See this SO answer to know how to plot timelines using ggplot2
# https://stackoverflow.com/a/9862712/2467938
#
setwd("/Users/sic2/git/sos/experiments")
library(ggplot2)
library(ggrepel)

dat <- read.csv("remote/nb_1_test5.tsv", header=TRUE, sep="\t")
dat <- dat[dat$StatsTYPE == 'thread',]

min_start = min(dat$User.Measure)

dat$User.Measure_2 - dat$User.Measure

ggplot(dat, aes(colour=Message)) + 
  geom_segment(aes(x=User.Measure - min_start, xend=User.Measure_2 - min_start, y=Message, yend=Message), size=5) +
  geom_label_repel(data= subset(dat), # , Message=='Thread_Policies' | Message=='Thread_Check_Policies'), 
                   aes(x=User.Measure - min_start, y=Message, label = Message),
                   nudge_x =0.05, nudge_y = .1) +
  theme_bw() +
  theme(legend.position="none") +
  theme(axis.text.x=element_text(angle=90,hjust=1), 
        axis.text=element_text(size=14),
        axis.title=element_text(size=16,face="bold")) +
  labs(title="Test title", x="Duration", y="Thread")
